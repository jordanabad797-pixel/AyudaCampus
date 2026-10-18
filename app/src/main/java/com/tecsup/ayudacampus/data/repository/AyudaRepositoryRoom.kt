package com.tecsup.ayudacampus.data.repository

import com.tecsup.ayudacampus.data.local.AyudaDatabase
import com.tecsup.ayudacampus.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

/**
 * [GUIADO] Implementación del contrato usando Room.
 * Las funciones de Categoría ya están hechas; el resto se completa en el Paso 4.
 */
class AyudaRepositoryRoom(db: AyudaDatabase) : AyudaRepository {

    private val categoriaDao = db.categoriaDao()
    // TODO (Paso 4): private val usuarioDao = db.usuarioDao()
    // TODO (Paso 4): private val ticketDao = db.ticketDao()

    // ---------- Categorías (ejemplo resuelto) ----------

    override fun observarCategorias(): Flow<List<Categoria>> = categoriaDao.observarTodas()

    override fun observarCategoriasActivas(): Flow<List<Categoria>> = categoriaDao.observarActivas()

    override suspend fun obtenerCategoria(id: Int): Categoria? = categoriaDao.obtener(id)

    /**
     * id == 0 → categoría nueva (INSERT). id > 0 → edición (UPDATE).
     * No usamos REPLACE para editar: REPLACE borra y vuelve a insertar la fila,
     * lo que chocaría con las claves foráneas de los tickets que la usan.
     */
    override suspend fun guardarCategoria(c: Categoria) {
        if (c.id == 0) categoriaDao.insertar(c) else categoriaDao.actualizar(c)
    }

    override suspend fun eliminarCategoria(c: Categoria) = categoriaDao.eliminar(c)

    override suspend fun cambiarActiva(id: Int, activa: Boolean) = categoriaDao.cambiarActiva(id, activa)

    // ---------- Usuarios, Tickets y Panel ----------
    // TODO (Paso 4): implementar las funciones restantes de AyudaRepository
}
