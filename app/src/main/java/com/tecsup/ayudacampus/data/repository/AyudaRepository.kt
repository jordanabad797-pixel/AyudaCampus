package com.tecsup.ayudacampus.data.repository

import com.tecsup.ayudacampus.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

/**
 * [ENTREGADO] El contrato de datos de la app.
 *
 * Los ViewModels SOLO conocen esta interface. Hoy la implementa [AyudaRepositoryRoom];
 * mañana (Semana 9) la implementará un repositorio web, sin tocar ViewModels ni pantallas.
 *
 * Las funciones de Usuarios, Tickets y Panel se agregan en los Pasos 2 y 4,
 * cuando ya existan las clases Usuario, TicketConDetalle, etc.
 */
interface AyudaRepository {

    // Usuarios
    // TODO (Paso 4): suspend fun login(correo: String, clave: String): Usuario?

    // Categorías (ejemplo resuelto)
    fun observarCategorias(): Flow<List<Categoria>>
    fun observarCategoriasActivas(): Flow<List<Categoria>>
    suspend fun obtenerCategoria(id: Int): Categoria?
    suspend fun guardarCategoria(c: Categoria)
    suspend fun eliminarCategoria(c: Categoria)
    suspend fun cambiarActiva(id: Int, activa: Boolean)

    // Tickets
    // TODO (Paso 4): crearTicket, observarTicketsCliente, observarTodosLosTickets, observarTicket,
    //  cambiarEstado, cambiarPrioridad, cerrarConCalificacion, reabrir, agregarComentario

    // Panel
    // TODO (Paso 4): observarCantidadPorEstado, observarCantidadPorCategoria, observarAltasSinAtender
}
