package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.estadoEnum
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoSla
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.FiltroEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * [TÚ LO HACES · Paso 7] Lista de tickets del estudiante.
 *
 * Regla de visibilidad: el estudiante solo ve SUS tickets (se consulta por clienteId).
 * El filtro y la búsqueda se aplican aquí con combine, sin tocar el DAO.
 */
class MisTicketsViewModel(
    repo: AyudaRepository,
    clienteId: Int
) : ViewModel() {

    /** Tickets del cliente tal como llegan de Room (null = todavía cargando). */
    private val misTickets: StateFlow<List<TicketConDetalle>?> =
        repo.observarTicketsCliente(clienteId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _filtro = MutableStateFlow(FiltroEstado.TODOS)
    val filtro: StateFlow<FiltroEstado> = _filtro.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    /** Lista final: combina los 3 flujos (tickets + filtro + texto de búsqueda). */
    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(misTickets, _filtro, _busqueda) { lista, filtro, texto ->
            lista.orEmpty().filter { filtro.acepta(it.ticket.estadoEnum) && coincide(it, texto) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** true mientras Room no entrega la primera lista. */
    val cargando: StateFlow<Boolean> = misTickets
        .map { it == null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    /** Para el saludo: tickets pendientes (ABIERTO o EN_PROCESO). */
    val abiertos: StateFlow<Int> = misTickets
        .map { lista ->
            lista.orEmpty().count {
                it.ticket.estadoEnum == EstadoTicket.ABIERTO || it.ticket.estadoEnum == EstadoTicket.EN_PROCESO
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Para el Perfil: RESUELTO + CERRADO. */
    val resueltos: StateFlow<Int> = misTickets
        .map { lista ->
            lista.orEmpty().count {
                it.ticket.estadoEnum == EstadoTicket.RESUELTO || it.ticket.estadoEnum == EstadoTicket.CERRADO
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val total: StateFlow<Int> = misTickets
        .map { it.orEmpty().size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** SLA de cada ticket, recalculado cada minuto. */
    val sla: StateFlow<Map<Int, EstadoSla>> =
        combine(misTickets, relojPorMinuto()) { lista, ahora -> lista.orEmpty().slaPorTicket(ahora) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun cambiarFiltro(filtro: FiltroEstado) {
        _filtro.value = filtro
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    /** Busca por título (sin importar mayúsculas) o por número: "12", "#12" o "#0012". */
    private fun coincide(item: TicketConDetalle, texto: String): Boolean {
        val consulta = texto.trim().removePrefix("#")
        if (consulta.isEmpty()) return true
        val porNumero = consulta.toIntOrNull()?.let { it == item.ticket.id } ?: false
        return porNumero || item.ticket.titulo.contains(consulta, ignoreCase = true)
    }
}
