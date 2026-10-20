package com.tecsup.ayudacampus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tecsup.ayudacampus.model.Rol

/**
 * [TÚ LO HACES · Paso 1] Tabla "comentarios": historial de conversación de un ticket.
 *
 * Relación: N comentarios → 1 ticket (ticketId → tickets.id).
 * Si se borra el ticket, se borran sus comentarios (CASCADE).
 *
 * ¿Por qué guardar autorNombre y autorRol aquí?
 * Es una "foto" del momento: la conversación se muestra con un solo SELECT,
 * sin buscar al autor en otra tabla, y no se rompe si el usuario cambia su nombre.
 * Por eso autorId NO es clave foránea.
 */
@Entity(
    tableName = "comentarios",
    foreignKeys = [
        ForeignKey(
            entity = Ticket::class,
            parentColumns = ["id"],
            childColumns = ["ticketId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ticketId")]
)
data class Comentario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticketId: Int,
    val autorId: Int,
    val autorNombre: String,
    val autorRol: String,
    val texto: String,
    val fecha: Long = System.currentTimeMillis(),
    /** Nota interna de Soporte: el estudiante nunca la ve. */
    val interno: Boolean = false
)

/** Rol del autor como enum (para dibujar la burbuja del lado correcto). */
val Comentario.autorRolEnum: Rol
    get() = Rol.desde(autorRol) ?: Rol.CLIENTE
