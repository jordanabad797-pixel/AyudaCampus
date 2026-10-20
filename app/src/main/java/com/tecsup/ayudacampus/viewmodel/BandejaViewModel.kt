package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.estadoEnum
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoSla
import com.tecsup.ayudacampus.model.FiltroEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Soporte ve TODOS los tickets. filter() conserva el orden por prioridad que entrega el DAO. */
class BandejaViewModel(repo: AyudaRepository) : ViewModel() {

    private val todos: StateFlow<List<TicketConDetalle>?> = repo.observarTodosLosTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _filtro = MutableStateFlow(FiltroEstado.TODOS)
    val filtro: StateFlow<FiltroEstado> = _filtro.asStateFlow()

    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(todos, _filtro) { lista, filtro ->
            lista.orEmpty().filter { filtro.acepta(it.ticket.estadoEnum) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cargando: StateFlow<Boolean> = todos
        .map { it == null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    /** SLA de cada ticket, recalculado cada minuto. */
    val sla: StateFlow<Map<Int, EstadoSla>> =
        combine(todos, relojPorMinuto()) { lista, ahora -> lista.orEmpty().slaPorTicket(ahora) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun cambiarFiltro(filtro: FiltroEstado) {
        _filtro.value = filtro
    }
}
