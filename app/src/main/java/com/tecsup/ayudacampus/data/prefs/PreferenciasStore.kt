package com.tecsup.ayudacampus.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tecsup.ayudacampus.model.Rol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Archivo "ajustes" de DataStore. Se declara UNA sola vez, a nivel de archivo:
 * así toda la app comparte la misma instancia (dos instancias del mismo archivo fallan).
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes")

/**
 * [TÚ LO HACES · Paso 5] Preferencias pequeñas del dispositivo (clave-valor).
 *
 * DataStore es SOLO para preferencias: perfil activo, modo oscuro, notificaciones...
 * Los tickets, comentarios y categorías NO van aquí: van en Room.
 *
 * Lectura → un Flow por preferencia (la UI se actualiza sola al cambiar).
 * Escritura → funciones suspend.
 */
class PreferenciasStore(private val context: Context) {

    private object Claves {
        val PERFIL_ACTIVO = stringPreferencesKey("perfilActivo")
        val USUARIO_ID = intPreferencesKey("usuarioId")
        val MODO_OSCURO = booleanPreferencesKey("modoOscuro")
        val NOTIFICACIONES = booleanPreferencesKey("notificaciones")
        val RECIBIR_TICKETS = booleanPreferencesKey("recibirTickets")
    }

    /** Si el archivo no se puede leer, usamos valores por defecto en lugar de cerrar la app. */
    private val datos: Flow<Preferences> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }

    // ---------------------------------------------------------------- Lectura

    /** "CLIENTE", "ADMIN" o "" (vacío = no hay sesión). */
    val perfilActivo: Flow<String> = datos
        .map { it[Claves.PERFIL_ACTIVO] ?: "" }
        .distinctUntilChanged()

    /** Id del usuario con sesión activa (0 = ninguno). */
    val usuarioId: Flow<Int> = datos
        .map { it[Claves.USUARIO_ID] ?: 0 }
        .distinctUntilChanged()

    /** Por defecto: tema claro. */
    val modoOscuro: Flow<Boolean> = datos
        .map { it[Claves.MODO_OSCURO] ?: false }
        .distinctUntilChanged()

    /** Por defecto: true. */
    val notificaciones: Flow<Boolean> = datos
        .map { it[Claves.NOTIFICACIONES] ?: true }
        .distinctUntilChanged()

    /** Por defecto: true. Si es false, la mesa de ayuda está cerrada y no se crean tickets. */
    val recibirTickets: Flow<Boolean> = datos
        .map { it[Claves.RECIBIR_TICKETS] ?: true }
        .distinctUntilChanged()

    // ---------------------------------------------------------------- Escritura

    /** Guarda el perfil y el usuario que iniciaron sesión (también se usa al "cambiar de perfil"). */
    suspend fun guardarSesion(rol: Rol, usuarioId: Int) {
        context.dataStore.edit {
            it[Claves.PERFIL_ACTIVO] = rol.name
            it[Claves.USUARIO_ID] = usuarioId
        }
    }

    suspend fun guardarModoOscuro(activo: Boolean) {
        context.dataStore.edit { it[Claves.MODO_OSCURO] = activo }
    }

    suspend fun guardarNotificaciones(activo: Boolean) {
        context.dataStore.edit { it[Claves.NOTIFICACIONES] = activo }
    }

    suspend fun guardarRecibirTickets(activo: Boolean) {
        context.dataStore.edit { it[Claves.RECIBIR_TICKETS] = activo }
    }

    /** Borra la sesión. El modo oscuro y los demás ajustes se conservan. */
    suspend fun cerrarSesion() {
        context.dataStore.edit {
            it.remove(Claves.PERFIL_ACTIVO)
            it.remove(Claves.USUARIO_ID)
        }
    }
}
