package com.tecsup.ayudacampus.data.repository

import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.model.ConteoCategoria
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.model.Rol
import kotlinx.coroutines.flow.Flow

/**
 * [ENTREGADO] El contrato de datos de la app.
 *
 * Los ViewModels SOLO conocen esta interface. Hoy la implementa [AyudaRepositoryRoom];
 * mañana (Semana 9) la implementará un repositorio web, sin tocar ViewModels ni pantallas.
 *
 * Regla de oro: lectura que actualiza la pantalla → Flow (sin suspend);
 * escritura o acción única → suspend.
 */
interface AyudaRepository {

    // Usuarios
    suspend fun login(correo: String, clave: String): Usuario?
    suspend fun obtenerUsuario(id: Int): Usuario?
    suspend fun obtenerUsuarioPorRol(rol: Rol): Usuario?

    // Categorías (ejemplo resuelto)
    fun observarCategorias(): Flow<List<Categoria>>
    fun observarCategoriasActivas(): Flow<List<Categoria>>
    suspend fun obtenerCategoria(id: Int): Categoria?
    suspend fun guardarCategoria(c: Categoria)
    suspend fun eliminarCategoria(c: Categoria)
    suspend fun cambiarActiva(id: Int, activa: Boolean)
    suspend fun contarTicketsDeCategoria(categoriaId: Int): Int

    // Tickets
    suspend fun crearTicket(
        clienteId: Int,
        categoriaId: Int,
        titulo: String,
        descripcion: String,
        ubicacion: String,
        prioridad: Prioridad
    ): Long
    fun observarTicketsCliente(clienteId: Int): Flow<List<TicketConDetalle>>
    fun observarTodosLosTickets(): Flow<List<TicketConDetalle>>
    fun observarTicket(id: Int): Flow<TicketConDetalle?>
    suspend fun cambiarEstado(ticketId: Int, estado: EstadoTicket)
    suspend fun cambiarPrioridad(ticketId: Int, prioridad: Prioridad)
    suspend fun cerrarConCalificacion(ticketId: Int, calificacion: Int)
    suspend fun reabrir(ticketId: Int)
    suspend fun agregarComentario(ticketId: Int, autor: Usuario, texto: String)

    // Panel
    fun observarCantidadPorEstado(estado: EstadoTicket): Flow<Int>
    fun observarCantidadPorCategoria(): Flow<List<ConteoCategoria>>
    fun observarAltasSinAtender(limite: Long): Flow<Int>
    /** Tiempo promedio de resolución en milisegundos (null si aún no hay tickets cerrados). */
    fun observarPromedioResolucion(): Flow<Long?>
}
