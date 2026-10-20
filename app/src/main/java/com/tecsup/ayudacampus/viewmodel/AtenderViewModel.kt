package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.entity.estadoEnum
import com.tecsup.ayudacampus.data.local.entity.prioridadEnum
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoSla
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Reglas de Soporte:
 *  - Puede dejar el ticket en ABIERTO, EN_PROCESO o RESUELTO y cambiar su prioridad.
 *  - NO puede cerrarlo (solo el estudiante, con calificación).
 *  - Un ticket CERRADO ya no se modifica ni se comenta.
 */
class AtenderViewModel(
    private val repo: AyudaRepository,
    private val ticketId: Int,
    private val soporte: Usuario
) : ViewModel() {

    val ticket: StateFlow<TicketConDetalle?> = repo.observarTicket(ticketId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sla: StateFlow<EstadoSla?> = combine(ticket, relojPorMinuto()) { t, ahora -> t?.sla(ahora) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _estadoElegido = MutableStateFlow<EstadoTicket?>(null)
    private val _prioridadElegida = MutableStateFlow<Prioridad?>(null)

    /** Lo que se muestra seleccionado: la elección de Soporte o, si no eligió, lo guardado. */
    val estado: StateFlow<EstadoTicket?> = combine(ticket, _estadoElegido) { t, elegido ->
        elegido ?: t?.ticket?.estadoEnum
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val prioridad: StateFlow<Prioridad?> = combine(ticket, _prioridadElegida) { t, elegida ->
        elegida ?: t?.ticket?.prioridadEnum
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val hayCambios: StateFlow<Boolean> =
        combine(ticket, _estadoElegido, _prioridadElegida) { t, e, p ->
            t != null && ((e != null && e != t.ticket.estadoEnum) || (p != null && p != t.ticket.prioridadEnum))
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _respuesta = MutableStateFlow("")
    val respuesta: StateFlow<String> = _respuesta.asStateFlow()

    private val _procesando = MutableStateFlow(false)
    val procesando: StateFlow<Boolean> = _procesando.asStateFlow()

    private val _mensaje = Channel<String>(Channel.BUFFERED)
    val mensaje: Flow<String> = _mensaje.receiveAsFlow()

    val soporteId: Int get() = soporte.id

    private val estaCerrado: Boolean
        get() = ticket.value?.ticket?.estadoEnum == EstadoTicket.CERRADO

    fun cambiarEstado(estado: EstadoTicket) {
        when {
            estaCerrado -> avisar("No se puede modificar un ticket cerrado")
            estado == EstadoTicket.CERRADO -> avisar("Solo el estudiante puede cerrar el ticket")
            else -> _estadoElegido.value = estado
        }
    }

    fun cambiarPrioridad(prioridad: Prioridad) {
        if (estaCerrado) avisar("No se puede modificar un ticket cerrado")
        else _prioridadElegida.value = prioridad
    }

    fun escribir(texto: String) {
        _respuesta.value = texto
    }

    fun responder(texto: String = _respuesta.value) {
        val limpio = texto.trim()
        if (limpio.isEmpty()) return
        if (estaCerrado) {
            avisar("El ticket está cerrado: ya no admite respuestas")
            return
        }
        ejecutar {
            repo.agregarComentario(ticketId, soporte, limpio)
            _respuesta.value = ""
        }
    }

    fun guardar() {
        val actual = ticket.value?.ticket ?: return
        if (estaCerrado) {
            avisar("No se puede modificar un ticket cerrado")
            return
        }
        val nuevoEstado = _estadoElegido.value?.takeIf { it != actual.estadoEnum }
        val nuevaPrioridad = _prioridadElegida.value?.takeIf { it != actual.prioridadEnum }
        if (nuevoEstado == null && nuevaPrioridad == null) return

        ejecutar {
            nuevoEstado?.let { repo.cambiarEstado(ticketId, it) }
            nuevaPrioridad?.let { repo.cambiarPrioridad(ticketId, it) }
            _estadoElegido.value = null
            _prioridadElegida.value = null
            _mensaje.send("Cambios guardados")
        }
    }

    private fun avisar(texto: String) {
        viewModelScope.launch { _mensaje.send(texto) }
    }

    private fun ejecutar(accion: suspend () -> Unit) {
        if (_procesando.value) return
        viewModelScope.launch {
            _procesando.value = true
            try {
                accion()
            } catch (e: Exception) {
                _mensaje.send("No se pudo completar la acción. Inténtalo de nuevo.")
            } finally {
                _procesando.value = false
            }
        }
    }
}
