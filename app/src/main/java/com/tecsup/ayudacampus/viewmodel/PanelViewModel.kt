package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.ui.components.graficos.DatoGrafico
import com.tecsup.ayudacampus.ui.theme.Violeta
import com.tecsup.ayudacampus.ui.theme.color
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class PanelViewModel(private val repo: AyudaRepository) : ViewModel() {

    val abiertos: StateFlow<Int> = contar(EstadoTicket.ABIERTO)
    val enProceso: StateFlow<Int> = contar(EstadoTicket.EN_PROCESO)
    val resueltos: StateFlow<Int> = contar(EstadoTicket.RESUELTO)
    val cerrados: StateFlow<Int> = contar(EstadoTicket.CERRADO)

    /** Milisegundos promedio entre la creación y el cierre (null = aún no hay tickets cerrados). */
    val promedioResolucion: StateFlow<Long?> = repo.observarPromedioResolucion()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val porEstado: StateFlow<List<DatoGrafico>> =
        combine(abiertos, enProceso, resueltos, cerrados) { a, p, r, c ->
            listOf(
                DatoGrafico(EstadoTicket.ABIERTO.etiqueta, a.toFloat(), EstadoTicket.ABIERTO.color()),
                DatoGrafico(EstadoTicket.EN_PROCESO.etiqueta, p.toFloat(), EstadoTicket.EN_PROCESO.color()),
                DatoGrafico(EstadoTicket.RESUELTO.etiqueta, r.toFloat(), EstadoTicket.RESUELTO.color()),
                DatoGrafico(EstadoTicket.CERRADO.etiqueta, c.toFloat(), EstadoTicket.CERRADO.color())
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** La barra con más tickets se resalta con el color fuerte; el resto, suave. */
    val porCategoria: StateFlow<List<DatoGrafico>> = repo.observarCantidadPorCategoria()
        .map { conteos ->
            val maximo = conteos.maxOfOrNull { it.cantidad } ?: 0
            conteos.map { c ->
                DatoGrafico(
                    etiqueta = c.nombre.substringBefore(" "),
                    valor = c.cantidad.toFloat(),
                    color = if (c.cantidad == maximo && maximo > 0) Violeta else Violeta.copy(alpha = 0.35f)
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** El límite (ahora − 1 hora) se recalcula cada minuto para que la alerta no quede desactualizada. */
    val altasSinAtender: StateFlow<Int> = limiteUnaHoraAtras()
        .flatMapLatest { limite -> repo.observarAltasSinAtender(limite) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun contar(estado: EstadoTicket): StateFlow<Int> =
        repo.observarCantidadPorEstado(estado)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun limiteUnaHoraAtras(): Flow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis() - UNA_HORA)
            delay(60_000)
        }
    }

    private companion object {
        const val UNA_HORA = 3_600_000L
    }
}
