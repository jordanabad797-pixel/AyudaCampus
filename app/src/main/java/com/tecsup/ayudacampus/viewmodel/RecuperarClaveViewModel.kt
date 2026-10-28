package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.rolEnum
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.model.SolicitudClave
import com.tecsup.ayudacampus.model.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecuperarClaveUiState(
    val correo: String = "",
    val error: String? = null,
    val enviando: Boolean = false,
    val enviada: Boolean = false
)

/**
 * "¿Olvidaste tu contraseña?" del estudiante.
 *  - Crea un ticket "Restablecer contraseña" (categoría Cuenta, prioridad Alta) para Soporte.
 *  - Respuesta siempre neutra: no revela si el correo está registrado.
 *  - No duplica la solicitud si ya hay una pendiente; las cuentas de Soporte no se restablecen aquí.
 */
class RecuperarClaveViewModel(private val repo: AyudaRepository) : ViewModel() {

    private val _ui = MutableStateFlow(RecuperarClaveUiState())
    val ui: StateFlow<RecuperarClaveUiState> = _ui.asStateFlow()

    fun cambiarCorreo(texto: String) = _ui.update { it.copy(correo = texto, error = null) }

    fun enviar() {
        val estado = _ui.value
        if (estado.enviando || estado.enviada) return

        val error = when {
            estado.correo.isBlank() -> "Ingresa tu correo"
            !Validaciones.esCorreoValido(estado.correo) -> "El correo no tiene un formato válido"
            else -> null
        }
        if (error != null) {
            _ui.update { it.copy(error = error) }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(enviando = true) }
            try {
                registrarSolicitud(Validaciones.normalizarCorreo(estado.correo))
                _ui.update { it.copy(enviada = true) }
            } catch (e: Exception) {
                _ui.update { it.copy(error = "No se pudo registrar la solicitud. Inténtalo de nuevo.") }
            } finally {
                _ui.update { it.copy(enviando = false) }
            }
        }
    }

    private suspend fun registrarSolicitud(correo: String) {
        val usuario = repo.obtenerUsuarioPorCorreo(correo) ?: return
        if (usuario.rolEnum != Rol.CLIENTE) return
        if (repo.contarPendientesConTitulo(usuario.id, SolicitudClave.TITULO) > 0) return

        val categoria = repo.obtenerCategoriaPorNombre(SolicitudClave.CATEGORIA)
            ?: repo.observarCategoriasActivas().first().firstOrNull()
            ?: return

        repo.crearTicket(
            clienteId = usuario.id,
            categoriaId = categoria.id,
            titulo = SolicitudClave.TITULO,
            descripcion = SolicitudClave.DESCRIPCION,
            ubicacion = SolicitudClave.UBICACION,
            prioridad = Prioridad.ALTA
        )
    }
}
