package com.tecsup.ayudacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tecsup.ayudacampus.data.local.entity.Usuario

/**
 * [TÚ LO HACES · Paso 2] Acceso a la tabla "usuarios".
 * Todas son acciones únicas → suspend.
 */
@Dao
interface UsuarioDao {

    /** Para recuperar la sesión guardada en DataStore (usuarioId). */
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtener(id: Int): Usuario?

    /** Para "Cambiar de perfil" en modo práctica: el primer usuario de ese rol. */
    @Query("SELECT * FROM usuarios WHERE rol = :rol ORDER BY id LIMIT 1")
    suspend fun primeroPorRol(rol: String): Usuario?

    @Query("SELECT COUNT(*) FROM usuarios WHERE correo = :correo COLLATE NOCASE")
    suspend fun contarPorCorreo(correo: String): Int

    /**
     * Para el login: la contraseña se compara después contra su hash (no en SQL).
     * COLLATE NOCASE: "Juan@Campus.test" y "juan@campus.test" son el mismo correo.
     */
    @Query("SELECT * FROM usuarios WHERE correo = :correo COLLATE NOCASE LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): Usuario?

    @Query("UPDATE usuarios SET clave = :clave, debeCambiarClave = :debeCambiar WHERE id = :id")
    suspend fun actualizarClave(id: Int, clave: String, debeCambiar: Boolean)

    /** ABORT: si el correo ya existe, el índice único lanza una excepción en lugar de reemplazar. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario): Long

    @Insert
    suspend fun insertarTodos(lista: List<Usuario>)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int
}
