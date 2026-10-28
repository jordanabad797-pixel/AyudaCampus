package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.rolEnum
import com.tecsup.ayudacampus.data.prefs.PreferenciasStore
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.model.Validaciones
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CambiarClaveUiState(
    val actual: String = "",
    val nueva: String = "",
    val confirmacion: String = "",
    val errorActual: String? = null,
    val errorNueva: String? = null,
    val errorConfirmacion: String? = null,
    val errorGeneral: String? = null,
    val guardando: Boolean = false
)

/**
 * Cambio de contraseña.
 * obligatorio = true → viene del login con una contraseña temporal: al terminar se inicia la sesión.
 * Reglas: la actual debe ser correcta; la nueva es segura, distinta de la actual,
 * no contiene el nombre ni el correo y se confirma.
 */
class CambiarClaveViewModel(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore,
    private val usuarioId: Int,
    val obligatorio: Boolean
) : ViewModel() {

    private val _ui = MutableStateFlow(CambiarClaveUiState())
    val ui: StateFlow<CambiarClaveUiState> = _ui.asStateFlow()

    /** Se emite con el rol del usuario cuando la contraseña se guardó. */
    private val _listo = Channel<Rol>(Channel.BUFFERED)
    val listo: Flow<Rol> = _listo.receiveAsFlow()

    fun cambiarActual(texto: String) = _ui.update { it.copy(actual = texto, errorActual = null, errorGeneral = null) }

    fun cambiarNueva(texto: String) = _ui.update {
        it.copy(nueva = texto, errorNueva = null, errorConfirmacion = null, errorGeneral = null)
    }

    fun cambiarConfirmacion(texto: String) = _ui.update {
        it.copy(confirmacion = texto, errorConfirmacion = null, errorGeneral = null)
    }

    fun guardar() {
        val estado = _ui.value
        if (estado.guardando) return

        val errorActual = when {
            estado.actual.isNotEmpty() -> null
            obligatorio -> "Ingresa la contraseña temporal"
            else -> "Ingresa tu contraseña actual"
        }
        val errorNueva = Validaciones.errorNuevaClave(estado.nueva)
            ?: if (estado.nueva == estado.actual) "La nueva contraseña debe ser distinta de la actual" else null
        val errorConfirmacion = when {
            estado.confirmacion.isEmpty() -> "Repite la nueva contraseña"
            estado.confirmacion != estado.nueva -> "Las contraseñas no coinciden"
            else -> null
        }
        if (listOf(errorActual, errorNueva, errorConfirmacion).any { it != null }) {
            _ui.update {
                it.copy(errorActual = errorActual, errorNueva = errorNueva, errorConfirmacion = errorConfirmacion)
            }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(guardando = true, errorGeneral = null) }
            try {
                val usuario = repo.obtenerUsuario(usuarioId)
                val errorPersonal = usuario?.let { Validaciones.errorNuevaClave(estado.nueva, listOf(it.nombre, it.correo)) }
                when {
                    usuario == null ->
                        _ui.update { it.copy(errorGeneral = "No se encontró la cuenta") }

                    errorPersonal != null ->
                        _ui.update { it.copy(errorNueva = errorPersonal) }

                    !repo.verificarClave(usuarioId, estado.actual) ->
                        _ui.update {
                            it.copy(
                                errorActual = if (obligatorio) "La contraseña temporal no es correcta"
                                else "La contraseña actual no es correcta"
                            )
                        }

                    else -> {
                        repo.actualizarClave(usuarioId, estado.nueva, debeCambiar = false)
                        if (obligatorio) prefs.guardarSesion(usuario.rolEnum, usuario.id)
                        _ui.update { CambiarClaveUiState() }
                        _listo.send(usuario.rolEnum)
                    }
                }
            } catch (e: Exception) {
                _ui.update { it.copy(errorGeneral = "No se pudo cambiar la contraseña. Inténtalo de nuevo.") }
            } finally {
                _ui.update { it.copy(guardando = false) }
            }
        }
    }
}
