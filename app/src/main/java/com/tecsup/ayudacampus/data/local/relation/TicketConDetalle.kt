package com.tecsup.ayudacampus.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.local.entity.Comentario
import com.tecsup.ayudacampus.data.local.entity.Ticket
import com.tecsup.ayudacampus.data.local.entity.Usuario

/**
 * [TÚ LO HACES · Paso 2] Un ticket con TODO lo que la pantalla necesita mostrar.
 *
 * No es una tabla: Room arma este objeto con varias consultas dentro de una
 * transacción (por eso las consultas del DAO que lo devuelven llevan @Transaction).
 *
 *  - @Embedded  → las columnas del propio ticket.
 *  - @Relation  → busca en otra tabla las filas cuyo entityColumn = parentColumn.
 */
data class TicketConDetalle(
    @Embedded
    val ticket: Ticket,

    /** 1 ticket → N comentarios (comentarios.ticketId = tickets.id). */
    @Relation(parentColumn = "id", entityColumn = "ticketId")
    val comentarios: List<Comentario>,

    /** N tickets → 1 categoría (categorias.id = tickets.categoriaId). */
    @Relation(parentColumn = "categoriaId", entityColumn = "id")
    val categoria: Categoria,

    /** N tickets → 1 cliente (usuarios.id = tickets.clienteId). */
    @Relation(parentColumn = "clienteId", entityColumn = "id")
    val cliente: Usuario
)

/**
 * La conversación en orden cronológico.
 * (@Relation no garantiza orden, así que lo ordenamos aquí.)
 */
val TicketConDetalle.conversacion: List<Comentario>
    get() = comentarios.sortedBy { it.fecha }
