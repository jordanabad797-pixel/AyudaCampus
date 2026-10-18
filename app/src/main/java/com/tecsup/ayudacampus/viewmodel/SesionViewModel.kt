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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * [TÚ LO HACES · Paso 6] Sesión de la app (compartido por toda la navegación).
 *
 * La sesión vive en DataStore (perfilActivo + usuarioId). [usuarioActual] se DERIVA de ahí:
 *  - login      → guarda la sesión  → usuarioActual pasa a tener valor.
 *  - cerrar     → borra la sesión   → usuarioActual vuelve a null.
 *  - (Paso 12) cambiar de perfil → guarda el otro perfil → usuarioActual cambia solo.
 * Así hay una sola fuente de verdad y la sesión sobrevive al cerrar la app.
 */
class SesionViewModel(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _sesionVerificada = MutableStateFlow(false)
    /** true cuando ya se leyó DataStore (para que el Splash sepa si saltar el login). */
    val sesionVerificada: StateFlow<Boolean> = _sesionVerificada.asStateFlow()

    /** Usuario con sesión activa, o null. Se valida que el rol guardado coincida con el de la base. */
    val usuarioActual: StateFlow<Usuario?> =
        combine(prefs.perfilActivo, prefs.usuarioId) { perfil, id -> perfil to id }
            .map { (perfil, id) ->
                if (perfil.isBlank() || id == 0) null
                else repo.obtenerUsuario(id)?.takeIf { it.rol == perfil }
            }
            .onEach { _sesionVerificada.value = true }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** Modo oscuro guardado (lo usa AyudaTheme en toda la app). */
    val modoOscuro: StateFlow<Boolean> =
        prefs.modoOscuro.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _rolSeleccionado = MutableStateFlow(Rol.CLIENTE)
    /** Pestaña elegida en el Login (también define el color del tema antes de ingresar). */
    val rolSeleccionado: StateFlow<Rol> = _rolSeleccionado.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    /** Evento de una sola vez: "el login fue exitoso, navega a la pantalla de este rol". */
    private val _ingresoExitoso = Channel<Rol>(Channel.BUFFERED)
    val ingresoExitoso: Flow<Rol> = _ingresoExitoso.receiveAsFlow()

    fun seleccionarRol(rol: Rol) {
        _rolSeleccionado.value = rol
        _error.value = null
    }

    fun limpiarError() {
        _error.value = null
    }

    /**
     * Valida y, si todo está bien, guarda la sesión en DataStore.
     * Reglas: campos obligatorios, formato de correo, credenciales correctas
     * y rol de la cuenta igual a la pestaña elegida.
     */
    fun login(correo: String, clave: String, rolSeleccionado: Rol = _rolSeleccionado.value) {
        if (_cargando.value) return // evita doble clic
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
                        _ingresoExitoso.send(usuario.rolEnum)
                    }
                }
            } catch (e: Exception) {
                _error.value = "No se pudo iniciar sesión. Inténtalo de nuevo."
            } finally {
                _cargando.value = false
            }
        }
    }

    /** Borra el perfil guardado; la navegación vuelve al Login. */
    fun cerrarSesion() {
        viewModelScope.launch { prefs.cerrarSesion() }
    }

    private companion object {
        val FORMATO_CORREO = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }
}
