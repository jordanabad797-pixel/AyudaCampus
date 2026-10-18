package com.tecsup.ayudacampus

import android.app.Application
import com.tecsup.ayudacampus.data.local.AyudaDatabase
import com.tecsup.ayudacampus.data.local.DatosIniciales
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.data.repository.AyudaRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * [GUIADO] Application de AyudaCampus (registrada en AndroidManifest con android:name).
 *
 * Es el "contenedor" de dependencias de la app: crea UNA sola base de datos y UN solo
 * Repository. Los ViewModels reciben [repository] (la interface), nunca la base ni los DAOs.
 */
class AyudaApp : Application() {

    /** Scope de la app para tareas de fondo (sembrar datos). SupervisorJob: un fallo no cancela al resto. */
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Base de datos Room (singleton). `lazy` = se crea la primera vez que se usa. */
    val database: AyudaDatabase by lazy { AyudaDatabase.obtener(this) }

    /** El contrato que usan los ViewModels. Hoy es Room; en la Semana 9 será un servicio web. */
    val repository: AyudaRepository by lazy { AyudaRepositoryRoom(database) }

    // TODO (Paso 5): val preferencias: PreferenciasStore by lazy { PreferenciasStore(this) }

    override fun onCreate() {
        super.onCreate()
        // Siembra los datos de prueba solo la primera vez (fuera del hilo principal).
        appScope.launch { DatosIniciales.sembrar(database) }
    }
}
