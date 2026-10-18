package com.tecsup.ayudacampus.model

/**
 * [ENTREGADO] Resultado de la consulta del panel "tickets por categoría"
 * (LEFT JOIN + COUNT). Room llena la clase por nombre de columna:
 * el SELECT debe devolver columnas llamadas `nombre` y `cantidad`.
 */
data class ConteoCategoria(
    val nombre: String,
    val cantidad: Int
)
