package com.tecsup.ayudacampus.model

/**
 * Filtros de las listas de tickets (FilterChip). Se usa en "Mis tickets" y en la Bandeja.
 * "Todos" incluye también los CERRADOS.
 */
enum class FiltroEstado(val etiqueta: String) {
    TODOS("Todos"),
    ABIERTOS("Abiertos"),
    EN_PROCESO("En proceso"),
    RESUELTOS("Resueltos");

    /** ¿Un ticket con este estado pasa el filtro? */
    fun acepta(estado: EstadoTicket): Boolean = when (this) {
        TODOS -> true
        ABIERTOS -> estado == EstadoTicket.ABIERTO
        EN_PROCESO -> estado == EstadoTicket.EN_PROCESO
        RESUELTOS -> estado == EstadoTicket.RESUELTO
    }
}
