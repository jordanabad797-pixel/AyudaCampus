package com.tecsup.ayudacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.ayudacampus.data.local.dao.CategoriaDao
import com.tecsup.ayudacampus.data.local.dao.TicketDao
import com.tecsup.ayudacampus.data.local.dao.UsuarioDao
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.local.entity.Comentario
import com.tecsup.ayudacampus.data.local.entity.Ticket
import com.tecsup.ayudacampus.data.local.entity.Usuario

/**
 * [GUIADO · Paso 3] Base de datos de AyudaCampus (archivo "ayudacampus.db").
 *
 * Registra las 4 tablas y expone un DAO por grupo de operaciones.
 * version 3: se agregó la columna comentarios.interno (notas internas de Soporte).
 * Si cambias una entidad después de ejecutar la app, sube [version] o desinstala la app.
 */
@Database(
    entities = [
        Categoria::class,
        Usuario::class,
        Ticket::class,
        Comentario::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AyudaDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun ticketDao(): TicketDao

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
