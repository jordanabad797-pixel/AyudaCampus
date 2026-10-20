package com.tecsup.ayudacampus.viewmodel

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val confirmacion: String = "",
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val errorConfirmacion: String? = null,
    val errorGeneral: String? = null,
    val enviando: Boolean = false
)

/**
 * Reglas del registro: nombre ≥ 3, correo válido y único, contraseña ≥ 4 y confirmación igual.
 * Si todo está bien crea la cuenta (rol CLIENTE) y deja la sesión iniciada en DataStore.
 */
class RegistroViewModel(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _ui = MutableStateFlow(RegistroUiState())
    val ui: StateFlow<RegistroUiState> = _ui.asStateFlow()

    private val _registrado = Channel<Unit>(Channel.BUFFERED)
    val registrado: Flow<Unit> = _registrado.receiveAsFlow()

    fun cambiarNombre(texto: String) = _ui.update { it.copy(nombre = texto, errorNombre = null, errorGeneral = null) }

    fun cambiarCorreo(texto: String) = _ui.update { it.copy(correo = texto, errorCorreo = null, errorGeneral = null) }

    fun cambiarClave(texto: String) = _ui.update {
        it.copy(clave = texto, errorClave = null, errorConfirmacion = null, errorGeneral = null)
    }

    fun cambiarConfirmacion(texto: String) = _ui.update {
        it.copy(confirmacion = texto, errorConfirmacion = null, errorGeneral = null)
    }

    fun registrar() {
        val estado = _ui.value
        if (estado.enviando) return

        val nombre = estado.nombre.trim()
        val errorNombre = when {
            nombre.length < Validaciones.MIN_NOMBRE -> "El nombre debe tener al menos ${Validaciones.MIN_NOMBRE} caracteres"
            nombre.length > Validaciones.MAX_NOMBRE -> "El nombre no puede pasar de ${Validaciones.MAX_NOMBRE} caracteres"
            else -> null
        }
        val errorCorreo = when {
            estado.correo.isBlank() -> "Ingresa tu correo"
            !Validaciones.esCorreoValido(estado.correo) -> "El correo no tiene un formato válido"
            else -> null
        }
        val errorClave = if (estado.clave.length < Validaciones.MIN_CLAVE) {
            "La contraseña debe tener al menos ${Validaciones.MIN_CLAVE} caracteres"
        } else null
        val errorConfirmacion = when {
            estado.confirmacion.isEmpty() -> "Repite tu contraseña"
            estado.confirmacion != estado.clave -> "Las contraseñas no coinciden"
            else -> null
        }

        if (listOf(errorNombre, errorCorreo, errorClave, errorConfirmacion).any { it != null }) {
            _ui.update {
                it.copy(
                    errorNombre = errorNombre,
                    errorCorreo = errorCorreo,
                    errorClave = errorClave,
                    errorConfirmacion = errorConfirmacion
                )
            }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(enviando = true, errorGeneral = null) }
            try {
                if (repo.existeCorreo(estado.correo)) {
                    _ui.update { it.copy(errorCorreo = CORREO_EN_USO) }
                    return@launch
                }
                val usuario = repo.registrarEstudiante(nombre, estado.correo, estado.clave)
                prefs.guardarSesion(Rol.CLIENTE, usuario.id)
                _registrado.send(Unit)
            } catch (e: SQLiteConstraintException) {
                _ui.update { it.copy(errorCorreo = CORREO_EN_USO) }
            } catch (e: Exception) {
                _ui.update { it.copy(errorGeneral = "No se pudo crear la cuenta. Inténtalo de nuevo.") }
            } finally {
                _ui.update { it.copy(enviando = false) }
            }
        }
    }

    private companion object {
        const val CORREO_EN_USO = "Ya existe una cuenta con este correo"
    }
}
