package com.tecsup.ayudacampus.viewmodel

import com.tecsup.ayudacampus.data.local.entity.estadoEnum
import com.tecsup.ayudacampus.data.local.entity.prioridadEnum
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.model.EstadoSla
import com.tecsup.ayudacampus.model.ReglasSla
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Emite la hora actual al inicio de cada minuto (solo mientras alguien la observa). */
internal fun relojPorMinuto(): Flow<Long> = flow {
    while (true) {
        val ahora = System.currentTimeMillis()
        emit(ahora)
        delay(60_000L - ahora % 60_000L)
    }
}

internal fun TicketConDetalle.sla(ahora: Long): EstadoSla? =
    ReglasSla.calcular(ticket.fecha, ticket.prioridadEnum, ticket.estadoEnum, ahora)

/** id del ticket → su SLA (los RESUELTOS y CERRADOS no aparecen). */
internal fun List<TicketConDetalle>.slaPorTicket(ahora: Long): Map<Int, EstadoSla> =
    mapNotNull { item -> item.sla(ahora)?.let { item.ticket.id to it } }.toMap()
