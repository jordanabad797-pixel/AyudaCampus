package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.entity.estadoEnum
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoSla
import com.tecsup.ayudacampus.model.EstadoTicket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Reglas del estudiante sobre su ticket:
 *  - Comentar: texto no vacío y ticket no CERRADO.
 *  - Cerrar: solo si está RESUELTO y con calificación de 1 a 5.
 *  - Reabrir: solo si está RESUELTO (vuelve a ABIERTO y borra la calificación).
 *  - Las notas internas de Soporte se quitan aquí: nunca llegan a la vista del estudiante.
 */
class TicketDetalleViewModel(
    private val repo: AyudaRepository,
    private val ticketId: Int,
    private val usuario: Usuario
) : ViewModel() {

    private val detalle: StateFlow<Resultado> = repo.observarTicket(ticketId)
        .map { t ->
            if (t != null && t.ticket.clienteId == usuario.id) Resultado.Listo(t.sinNotasInternas())
            else Resultado.NoEncontrado
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resultado.Cargando)

    val ticket: StateFlow<TicketConDetalle?> = detalle
        .map { (it as? Resultado.Listo)?.ticket }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val noEncontrado: StateFlow<Boolean> = detalle
        .map { it is Resultado.NoEncontrado }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val sla: StateFlow<EstadoSla?> = combine(ticket, relojPorMinuto()) { t, ahora -> t?.sla(ahora) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _calificacion = MutableStateFlow(0)
    val calificacion: StateFlow<Int> = _calificacion.asStateFlow()

    private val _comentarioFinal = MutableStateFlow("")
    val comentarioFinal: StateFlow<String> = _comentarioFinal.asStateFlow()

    private val _procesando = MutableStateFlow(false)
    val procesando: StateFlow<Boolean> = _procesando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val usuarioId: Int get() = usuario.id

    private val estadoActual: EstadoTicket?
        get() = ticket.value?.ticket?.estadoEnum

    fun escribir(texto: String) {
        _mensaje.value = texto
    }

    fun calificar(estrellas: Int) {
        _calificacion.value = estrellas.coerceIn(0, 5)
    }

    fun escribirComentarioFinal(texto: String) {
        _comentarioFinal.value = texto
    }

    fun limpiarError() {
        _error.value = null
    }

    fun comentar(texto: String = _mensaje.value) {
        val limpio = texto.trim()
        if (limpio.isEmpty() || estadoActual == null || estadoActual == EstadoTicket.CERRADO) return
        ejecutar {
            repo.agregarComentario(ticketId, usuario, limpio)
            _mensaje.value = ""
        }
    }

    fun cerrar(calificacion: Int = _calificacion.value) {
        if (estadoActual != EstadoTicket.RESUELTO) {
            _error.value = "Solo puedes cerrar un ticket resuelto"
            return
        }
        if (calificacion !in 1..5) {
            _error.value = "Califica la atención de 1 a 5 estrellas"
            return
        }
        ejecutar {
            val final = _comentarioFinal.value.trim()
            if (final.isNotEmpty()) repo.agregarComentario(ticketId, usuario, final)
            repo.cerrarConCalificacion(ticketId, calificacion)
            _comentarioFinal.value = ""
        }
    }

    fun reabrir() {
        if (estadoActual != EstadoTicket.RESUELTO) {
            _error.value = "Solo puedes reabrir un ticket resuelto"
            return
        }
        ejecutar {
            repo.reabrir(ticketId)
            _calificacion.value = 0
        }
    }

    private fun ejecutar(accion: suspend () -> Unit) {
        if (_procesando.value) return
        viewModelScope.launch {
            _procesando.value = true
            try {
                accion()
            } catch (e: Exception) {
                _error.value = "No se pudo completar la acción. Inténtalo de nuevo."
            } finally {
                _procesando.value = false
            }
        }
    }

    private sealed interface Resultado {
        data object Cargando : Resultado
        data object NoEncontrado : Resultado
        data class Listo(val ticket: TicketConDetalle) : Resultado
    }
}
