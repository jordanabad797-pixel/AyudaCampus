package com.tecsup.ayudacampus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad

/**
 * [TÚ LO HACES · Paso 1] Tabla "tickets": una incidencia reportada por un estudiante.
 *
 * Relaciones:
 *  - N tickets → 1 cliente   (clienteId   → usuarios.id)
 *  - N tickets → 1 categoría (categoriaId → categorias.id)
 *
 * Decisiones:
 *  - Si se borra un usuario, se borran sus tickets (CASCADE).
 *  - Una categoría con tickets NO se puede borrar (RESTRICT): protege el historial.
 *    El ViewModel mostrará un mensaje antes de que la base lo rechace (Paso 11).
 *  - prioridad y estado se guardan como texto (enum.name).
 *  - fechaCierre y calificacion son null hasta que el estudiante cierra el ticket.
 */
@Entity(
    tableName = "tickets",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    // Cada columna FK necesita su índice (evita advertencias de Room y acelera los JOIN).
    indices = [Index("clienteId"), Index("categoriaId")]
)
data class Ticket(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clienteId: Int,
    val categoriaId: Int,
    val titulo: String,
    val descripcion: String,
    val ubicacion: String,
    val prioridad: String = Prioridad.MEDIA.name,
    val estado: String = EstadoTicket.ABIERTO.name,
    val fecha: Long = System.currentTimeMillis(),
    val fechaCierre: Long? = null,
    val calificacion: Int? = null
)

/** Estado como enum (para reglas de negocio y UI). */
val Ticket.estadoEnum: EstadoTicket
    get() = EstadoTicket.desde(estado)

/** Prioridad como enum (para reglas de negocio y UI). */
val Ticket.prioridadEnum: Prioridad
    get() = Prioridad.desde(prioridad)
