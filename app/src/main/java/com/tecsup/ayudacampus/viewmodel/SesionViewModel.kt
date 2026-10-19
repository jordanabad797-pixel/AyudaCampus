package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.entity.rolEnum
import com.tecsup.ayudacampus.data.prefs.PreferenciasStore
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.Rol
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Sesión y preferencias de la app (compartido por toda la navegación).
 *
 * La sesión vive en DataStore (perfilActivo + usuarioId) y [usuarioActual] se deriva de ahí:
 * login, cerrar sesión y cambiar de perfil solo escriben en DataStore y el usuario se actualiza solo.
 */
class SesionViewModel(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _sesionVerificada = MutableStateFlow(false)
    val sesionVerificada: StateFlow<Boolean> = _sesionVerificada.asStateFlow()

    val usuarioActual: StateFlow<Usuario?> =
        combine(prefs.perfilActivo, prefs.usuarioId) { perfil, id -> perfil to id }
            .map { (perfil, id) ->
                if (perfil.isBlank() || id == 0) null
                else repo.obtenerUsuario(id)?.takeIf { it.rol == perfil }
            }
            .onEach { _sesionVerificada.value = true }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val modoOscuro: StateFlow<Boolean> =
        prefs.modoOscuro.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val notificaciones: StateFlow<Boolean> =
        prefs.notificaciones.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val recibirTickets: StateFlow<Boolean> =
        prefs.recibirTickets.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private val _rolSeleccionado = MutableStateFlow(Rol.CLIENTE)
    val rolSeleccionado: StateFlow<Rol> = _rolSeleccionado.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    /** Evento de una sola vez: navegar a la pantalla principal de este rol (login o cambio de perfil). */
    private val _irAInicio = Channel<Rol>(Channel.BUFFERED)
    val irAInicio: Flow<Rol> = _irAInicio.receiveAsFlow()

    private val _aviso = Channel<String>(Channel.BUFFERED)
    val aviso: Flow<String> = _aviso.receiveAsFlow()

    fun seleccionarRol(rol: Rol) {
        _rolSeleccionado.value = rol
        _error.value = null
    }

    fun limpiarError() {
        _error.value = null
    }

    fun login(correo: String, clave: String, rolSeleccionado: Rol = _rolSeleccionado.value) {
        if (_cargando.value) return
        val correoLimpio = correo.trim()

        val errorValidacion = when {
            correoLimpio.isEmpty() && clave.isEmpty() -> "Ingresa tu correo y tu contraseña"
            correoLimpio.isEmpty() -> "Ingresa tu correo"
            clave.isEmpty() -> "Ingresa tu contraseña"
            !FORMATO_CORREO.matches(correoLimpio) -> "El correo no tiene un formato válido"
            else -> null
        }
        if (errorValidacion != null) {
            _error.value = errorValidacion
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val usuario = repo.login(correoLimpio, clave)
                when {
                    usuario == null ->
                        _error.value = "Correo o contraseña incorrectos"

                    usuario.rolEnum != rolSeleccionado ->
                        _error.value = "Esta cuenta es de ${usuario.rolEnum.etiqueta}. " +
                            "Elige la pestaña «${usuario.rolEnum.etiqueta}»."

                    else -> {
                        prefs.guardarSesion(usuario.rolEnum, usuario.id)
                        _irAInicio.send(usuario.rolEnum)
                    }
                }
            } catch (e: Exception) {
                _error.value = "No se pudo iniciar sesión. Inténtalo de nuevo."
            } finally {
                _cargando.value = false
            }
        }
    }

    /** Modo práctica: guarda el otro perfil en DataStore y navega a su pantalla principal. */
    fun cambiarPerfil() {
        val actual = usuarioActual.value ?: return
        val destino = actual.rolEnum.otro()
        viewModelScope.launch {
            val otroUsuario = repo.obtenerUsuarioPorRol(destino)
            if (otroUsuario == null) {
                _aviso.send("No hay una cuenta de ${destino.etiqueta} en este dispositivo")
                return@launch
            }
            prefs.guardarSesion(destino, otroUsuario.id)
            _rolSeleccionado.value = destino
            _irAInicio.send(destino)
        }
    }

    fun cambiarModoOscuro(activo: Boolean) {
        viewModelScope.launch { prefs.guardarModoOscuro(activo) }
    }

    fun cambiarNotificaciones(activo: Boolean) {
        viewModelScope.launch { prefs.guardarNotificaciones(activo) }
    }

    fun cambiarRecibirTickets(activo: Boolean) {
        viewModelScope.launch { prefs.guardarRecibirTickets(activo) }
    }

    fun cerrarSesion() {
        viewModelScope.launch { prefs.cerrarSesion() }
    }

    private companion object {
        val FORMATO_CORREO = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }
}
