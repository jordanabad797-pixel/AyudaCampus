package com.tecsup.ayudacampus.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * [ENTREGADO] Todas las rutas de navegación en un solo lugar (evita strings sueltos).
 * Las rutas con {argumento} se construyen con las funciones de abajo.
 */
object Rutas {
    // Comunes
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"

    // Grafos (uno por perfil)
    const val GRAFO_CLIENTE = "grafo_cliente"
    const val GRAFO_SOPORTE = "grafo_soporte"

    // Argumentos
    const val ARG_TICKET_ID = "ticketId"
    const val ARG_CATEGORIA_ID = "categoriaId"

    // Estudiante
    const val MIS_TICKETS = "cliente/tickets"
    const val NUEVO_TICKET = "cliente/nuevo"
    const val DETALLE_TICKET = "cliente/ticket/{$ARG_TICKET_ID}"
    const val PERFIL = "cliente/perfil"

    // Soporte TI
    const val PANEL = "soporte/panel"
    const val BANDEJA = "soporte/bandeja"
    const val ATENDER = "soporte/atender/{$ARG_TICKET_ID}"
    const val CATEGORIAS = "soporte/categorias"
    const val FORM_CATEGORIA = "soporte/categoria?$ARG_CATEGORIA_ID={$ARG_CATEGORIA_ID}"
    const val AJUSTES = "soporte/ajustes"

    fun detalleTicket(id: Int) = "cliente/ticket/$id"
    fun atender(id: Int) = "soporte/atender/$id"

    /** Sin id → crear categoría. Con id → editarla. */
    fun formCategoria(id: Int? = null) =
        if (id == null) "soporte/categoria" else "soporte/categoria?$ARG_CATEGORIA_ID=$id"
}

/** Un ítem de la barra de navegación inferior. */
data class ItemNav(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector
)

/** Barra inferior del Estudiante: 3 ítems. */
val itemsNavCliente = listOf(
    ItemNav(Rutas.MIS_TICKETS, "Tickets", Icons.Outlined.ConfirmationNumber),
    ItemNav(Rutas.NUEVO_TICKET, "Nuevo", Icons.Outlined.AddCircleOutline),
    ItemNav(Rutas.PERFIL, "Perfil", Icons.Outlined.Person)
)

/** Barra inferior de Soporte TI: 4 ítems. */
val itemsNavSoporte = listOf(
    ItemNav(Rutas.PANEL, "Panel", Icons.Outlined.BarChart),
    ItemNav(Rutas.BANDEJA, "Tickets", Icons.Outlined.Inbox),
    ItemNav(Rutas.CATEGORIAS, "Categorías", Icons.Outlined.LocalOffer),
    ItemNav(Rutas.AJUSTES, "Ajustes", Icons.Outlined.Settings)
)
