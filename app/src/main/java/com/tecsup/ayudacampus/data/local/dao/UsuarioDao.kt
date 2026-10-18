package com.tecsup.ayudacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tecsup.ayudacampus.data.local.entity.Usuario

/**
 * [TÚ LO HACES · Paso 2] Acceso a la tabla "usuarios".
 * Todas son acciones únicas → suspend.
 */
@Dao
interface UsuarioDao {

    /**
     * Devuelve el usuario si correo y clave coinciden; null si no.
     * COLLATE NOCASE: "Juan@Campus.test" y "juan@campus.test" son el mismo correo.
     */
    @Query("SELECT * FROM usuarios WHERE correo = :correo COLLATE NOCASE AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Usuario?

    /** Para recuperar la sesión guardada en DataStore (usuarioId). */
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtener(id: Int): Usuario?

    @Insert
    suspend fun insertarTodos(lista: List<Usuario>)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int
}
