package com.tecsup.ayudacampus.viewmodel

import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle

/** Lo que ve el estudiante: la conversación sin las notas internas de Soporte. */
internal fun TicketConDetalle.sinNotasInternas(): TicketConDetalle =
    copy(comentarios = comentarios.filterNot { it.interno })
