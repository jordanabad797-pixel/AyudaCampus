package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.local.entity.estadoEnum
import com.tecsup.ayudacampus.data.local.entity.prioridadEnum
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoSla
import com.tecsup.ayudacampus.model.FiltroEstado
import com.tecsup.ayudacampus.model.Prioridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.text.Normalizer

/** Criterios de la búsqueda avanzada (se aplican todos a la vez). */
data class FiltrosBandeja(
    val estado: FiltroEstado = FiltroEstado.TODOS,
    val prioridad: Prioridad? = null,
    val categoriaId: Int? = null,
    val texto: String = ""
) {
    val activos: Int
        get() = listOf(
            estado != FiltroEstado.TODOS,
            prioridad != null,
            categoriaId != null,
            texto.isNotBlank()
        ).count { it }
}

/**
 * Soporte ve TODOS los tickets. Los filtros se aplican con filter(), que conserva
 * el orden por prioridad (ALTA → MEDIA → BAJA, más reciente primero) que entrega el DAO.
 */
class BandejaViewModel(repo: AyudaRepository) : ViewModel() {

    private val todos: StateFlow<List<TicketConDetalle>?> = repo.observarTodosLosTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val categorias: StateFlow<List<Categoria>> = repo.observarCategorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _filtros = MutableStateFlow(FiltrosBandeja())
    val filtros: StateFlow<FiltrosBandeja> = _filtros.asStateFlow()

    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(todos, _filtros) { lista, filtros ->
            val consulta = normalizar(filtros.texto.trim().removePrefix("#"))
            lista.orEmpty().filter { cumple(it, filtros, consulta) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val total: StateFlow<Int> = todos
        .map { it.orEmpty().size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cargando: StateFlow<Boolean> = todos
        .map { it == null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    /** SLA de cada ticket, recalculado cada minuto. */
    val sla: StateFlow<Map<Int, EstadoSla>> =
        combine(todos, relojPorMinuto()) { lista, ahora -> lista.orEmpty().slaPorTicket(ahora) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun cambiarFiltro(estado: FiltroEstado) = _filtros.update { it.copy(estado = estado) }

    /** Tocar la prioridad ya elegida la quita. */
    fun cambiarPrioridad(prioridad: Prioridad) = _filtros.update {
        it.copy(prioridad = if (it.prioridad == prioridad) null else prioridad)
    }

    /** Tocar la categoría ya elegida la quita. */
    fun cambiarCategoria(categoriaId: Int) = _filtros.update {
        it.copy(categoriaId = if (it.categoriaId == categoriaId) null else categoriaId)
    }

    fun buscar(texto: String) = _filtros.update { it.copy(texto = texto.take(MAX_TEXTO)) }

    fun limpiarFiltros() {
        _filtros.value = FiltrosBandeja()
    }

    private fun cumple(item: TicketConDetalle, filtros: FiltrosBandeja, consulta: String): Boolean {
        val t = item.ticket
        if (!filtros.estado.acepta(t.estadoEnum)) return false
        if (filtros.prioridad != null && t.prioridadEnum != filtros.prioridad) return false
        if (filtros.categoriaId != null && t.categoriaId != filtros.categoriaId) return false
        if (consulta.isEmpty()) return true

        val porNumero = consulta.toIntOrNull()?.let { it == t.id } ?: false
        return porNumero ||
            normalizar(t.titulo).contains(consulta) ||
            normalizar(item.cliente.nombre).contains(consulta)
    }

    /** Minúsculas y sin tildes: "Impresión" y "impresion" coinciden. */
    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replace(MARCAS_DIACRITICAS, "")
            .lowercase()

    private companion object {
        const val MAX_TEXTO = 60
        val MARCAS_DIACRITICAS = Regex("\\p{Mn}+")
    }
}
