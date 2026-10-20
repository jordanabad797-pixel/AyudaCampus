package com.tecsup.ayudacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.tecsup.ayudacampus.data.local.entity.Comentario
import com.tecsup.ayudacampus.data.local.entity.Ticket
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.model.ConteoCategoria
import kotlinx.coroutines.flow.Flow

/**
 * [TÚ LO HACES · Paso 2] Acceso a "tickets" y "comentarios".
 *
 * Regla de oro:
 *  - Escritura / acción única → suspend.
 *  - Lectura que debe actualizar la pantalla → Flow (sin suspend).
 *
 * Los textos 'ALTA', 'ABIERTO', 'CERRADO'... son los valores de enum.name
 * que se guardan en la tabla (Prioridad.ALTA.name, EstadoTicket.CERRADO.name...).
 */
@Dao
interface TicketDao {

    // ---------------------------------------------------------------- Escritura

    @Insert
    suspend fun insertarTicket(t: Ticket): Long

    @Insert
    suspend fun insertarComentario(c: Comentario)

    @Query("UPDATE tickets SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    @Query("UPDATE tickets SET prioridad = :prioridad WHERE id = :id")
    suspend fun actualizarPrioridad(id: Int, prioridad: String)

    /** Cierra el ticket: guarda la calificación y la fecha de cierre. */
    @Query("UPDATE tickets SET estado = 'CERRADO', calificacion = :calificacion, fechaCierre = :fechaCierre WHERE id = :id")
    suspend fun cerrar(id: Int, calificacion: Int, fechaCierre: Long)

    /** Reabre el ticket: vuelve a ABIERTO y borra calificación y fecha de cierre. */
    @Query("UPDATE tickets SET estado = 'ABIERTO', calificacion = NULL, fechaCierre = NULL WHERE id = :id")
    suspend fun reabrir(id: Int)

    // ---------------------------------------------------------------- Lectura (Flow)

    /** Tickets de un estudiante: el más reciente primero. */
    @Transaction
    @Query("SELECT * FROM tickets WHERE clienteId = :clienteId ORDER BY fecha DESC")
    fun observarPorCliente(clienteId: Int): Flow<List<TicketConDetalle>>

    /** Bandeja de Soporte: ALTA → MEDIA → BAJA y, dentro de cada una, el más reciente primero. */
    @Transaction
    @Query(
        """
        SELECT * FROM tickets
        ORDER BY CASE prioridad WHEN 'ALTA' THEN 0 WHEN 'MEDIA' THEN 1 ELSE 2 END,
                 fecha DESC
        """
    )
    fun observarTodos(): Flow<List<TicketConDetalle>>

    /** Un ticket con su conversación; se actualiza solo cuando cambia. */
    @Transaction
    @Query("SELECT * FROM tickets WHERE id = :id")
    fun observarUno(id: Int): Flow<TicketConDetalle?>

    // ---------------------------------------------------------------- Panel

    @Query("SELECT COUNT(*) FROM tickets WHERE estado = :estado")
    fun contarPorEstado(estado: String): Flow<Int>

    /**
     * Tickets por categoría. LEFT JOIN + COUNT(t.id) para que también aparezcan
     * las categorías SIN tickets (con 0). Los alias coinciden con ConteoCategoria.
     */
    @Query(
        """
        SELECT c.nombre AS nombre, COUNT(t.id) AS cantidad
        FROM categorias c
        LEFT JOIN tickets t ON t.categoriaId = c.id
        GROUP BY c.id, c.nombre
        ORDER BY c.nombre
        """
    )
    fun contarPorCategoria(): Flow<List<ConteoCategoria>>

    /** Alerta: tickets ALTA todavía ABIERTOS creados antes de [limite] (ahora − 1 hora). */
    @Query("SELECT COUNT(*) FROM tickets WHERE prioridad = 'ALTA' AND estado = 'ABIERTO' AND fecha < :limite")
    fun contarAltasSinAtender(limite: Long): Flow<Int>

    /** Promedio en milisegundos de (fechaCierre − fecha) de los tickets CERRADOS; null si no hay ninguno. */
    @Query("SELECT AVG(fechaCierre - fecha) FROM tickets WHERE estado = 'CERRADO' AND fechaCierre IS NOT NULL")
    fun promedioResolucion(): Flow<Double?>

    /** Para la regla "no se puede eliminar una categoría que ya tiene tickets" (Paso 11). */
    @Query("SELECT COUNT(*) FROM tickets WHERE categoriaId = :categoriaId")
    suspend fun contarDeCategoria(categoriaId: Int): Int
}
