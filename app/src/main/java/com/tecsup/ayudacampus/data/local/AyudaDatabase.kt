package com.tecsup.ayudacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.ayudacampus.data.local.dao.CategoriaDao
import com.tecsup.ayudacampus.data.local.entity.Categoria

/**
 * [GUIADO] Base de datos de AyudaCampus (archivo "ayudacampus.db").
 *
 * Si cambias una entidad después de haber ejecutado la app, sube [version]
 * o desinstala la app del emulador (el esquema debe coincidir).
 */
@Database(
    entities = [Categoria::class /* TODO (Paso 3): Usuario::class, Ticket::class, Comentario::class */],
    version = 1,
    exportSchema = false
)
abstract class AyudaDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    // TODO (Paso 3): abstract fun usuarioDao(): UsuarioDao
    // TODO (Paso 3): abstract fun ticketDao(): TicketDao

    companion object {
        private const val NOMBRE_BD = "ayudacampus.db"

        @Volatile
        private var INSTANCE: AyudaDatabase? = null

        /** Devuelve la única instancia (patrón singleton con doble verificación). */
        fun obtener(context: Context): AyudaDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AyudaDatabase::class.java,
                    NOMBRE_BD
                )
                    // Modo práctica: si sube la versión, se recrea la base (y se vuelve a sembrar).
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
