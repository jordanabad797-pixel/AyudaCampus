package com.tecsup.ayudacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tecsup.ayudacampus.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

/**
 * [ENTREGADO] Ejemplo resuelto de DAO.
 * Regla de oro: lectura que actualiza la pantalla → Flow (sin suspend);
 * escritura o acción única → suspend.
 */
@Dao
interface CategoriaDao {

    @Query("SELECT * FROM categorias ORDER BY nombre")
    fun observarTodas(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias WHERE activa = 1 ORDER BY nombre")
    fun observarActivas(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun obtener(id: Int): Categoria?

    @Query("SELECT COUNT(*) FROM categorias")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(c: Categoria): Long

    @Insert
    suspend fun insertarTodas(lista: List<Categoria>)

    @Update
    suspend fun actualizar(c: Categoria)

    @Delete
    suspend fun eliminar(c: Categoria)

    @Query("UPDATE categorias SET activa = :activa WHERE id = :id")
    suspend fun cambiarActiva(id: Int, activa: Boolean)
}
