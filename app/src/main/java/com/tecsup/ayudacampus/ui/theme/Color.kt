package com.tecsup.ayudacampus.ui.theme

import androidx.compose.ui.graphics.Color
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad

/*
 * [ENTREGADO] Paleta de AyudaCampus (sección 8 del laboratorio).
 * En las pantallas NO uses estos valores directamente: usa MaterialTheme.colorScheme.*
 * Solo los colores semánticos (estado, prioridad, éxito, estrellas) se usan por nombre,
 * a través de las funciones color() de abajo.
 */

// ---------- Estudiante (cian) ----------
val Cian = Color(0xFF0891B2)
val CianContenedor = Color(0xFFCFFAFE)
val CianSobreContenedor = Color(0xFF164E63)
val CianFondo = Color(0xFFF7FDFE)

val CianOscuro = Color(0xFF22D3EE)
val CianSobreOscuro = Color(0xFF00363F)
val CianContenedorOscuro = Color(0xFF155E75)
val CianFondoOscuro = Color(0xFF0B1417)
val CianSuperficieOscura = Color(0xFF121D21)
val CianVarianteOscura = Color(0xFF1C2A30)

// ---------- Soporte TI (violeta) ----------
val Violeta = Color(0xFF7C3AED)
val VioletaContenedor = Color(0xFFEDE4FF)
val VioletaSobreContenedor = Color(0xFF3B0764)
val VioletaFondo = Color(0xFFFAF8FF)

val VioletaOscuro = Color(0xFFA78BFA)
val VioletaSobreOscuro = Color(0xFF2E1065)
val VioletaContenedorOscuro = Color(0xFF4C2A8C)
val VioletaFondoOscuro = Color(0xFF110E17)
val VioletaSuperficieOscura = Color(0xFF1A1622)
val VioletaVarianteOscura = Color(0xFF262033)

// ---------- Neutros ----------
val TextoPrincipal = Color(0xFF0F172A)
val TextoSecundario = Color(0xFF64748B)
val Borde = Color(0xFFCBD5E1)
val BordeSuave = Color(0xFFE2E8F0)
val SuperficieVariante = Color(0xFFF1F5F9)
val TextoPrincipalOscuro = Color(0xFFE2E8F0)
val TextoSecundarioOscuro = Color(0xFF94A3B8)
val BordeOscuro = Color(0xFF475569)
val BordeSuaveOscuro = Color(0xFF2A3640)

// ---------- Estados del ticket ----------
val EstadoAbierto = Color(0xFFD97706)   // ámbar
val EstadoEnProceso = Color(0xFF2563EB) // azul
val EstadoResuelto = Color(0xFF16A34A)  // verde
val EstadoCerrado = Color(0xFF6B7280)   // gris

// ---------- Prioridades ----------
val PrioridadAlta = Color(0xFFDC2626)  // rojo
val PrioridadMedia = Color(0xFFB45309) // café
val PrioridadBaja = Color(0xFF6B7280)  // gris

// ---------- Otros semánticos ----------
val Exito = Color(0xFF16A34A)
val Peligro = Color(0xFFDC2626)
val Estrella = Color(0xFFF59E0B)

/** Color semántico de cada estado (chips, stepper, gráficos). */
fun EstadoTicket.color(): Color = when (this) {
    EstadoTicket.ABIERTO -> EstadoAbierto
    EstadoTicket.EN_PROCESO -> EstadoEnProceso
    EstadoTicket.RESUELTO -> EstadoResuelto
    EstadoTicket.CERRADO -> EstadoCerrado
}

/** Color semántico de cada prioridad (chips y franja de TicketCard). */
fun Prioridad.color(): Color = when (this) {
    Prioridad.ALTA -> PrioridadAlta
    Prioridad.MEDIA -> PrioridadMedia
    Prioridad.BAJA -> PrioridadBaja
}
