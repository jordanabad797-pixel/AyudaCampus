package com.tecsup.ayudacampus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [ENTREGADO] Ejemplo resuelto: una entidad = una tabla.
 * Cada propiedad es una columna; [id] lo genera SQLite.
 */
@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val icono: String, // pc, code, wifi, key, printer, tag
    val activa: Boolean = true
)
