package com.tecsup.ayudacampus.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** [ENTREGADO] Utilidades de formato para mostrar datos en pantalla. */
object Formatos {

    private val local = Locale.forLanguageTag("es-PE")

    /** 12 → "#0012" */
    fun numero(id: Int): String = "#" + id.toString().padStart(4, '0')

    /** Tiempo relativo para las tarjetas: "Ahora", "Hace 20 min", "Hace 2 h", "Ayer", "Hace 3 d". */
    fun haceCuanto(fecha: Long, ahora: Long = System.currentTimeMillis()): String {
        val minutos = (ahora - fecha).coerceAtLeast(0L) / 60_000L
        return when {
            minutos < 1 -> "Ahora"
            minutos < 60 -> "Hace $minutos min"
            minutos < 60 * 24 -> "Hace ${minutos / 60} h"
            minutos < 60 * 48 -> "Ayer"
            else -> "Hace ${minutos / (60 * 24)} d"
        }
    }

    /** Hora de un comentario: "10:32" */
    fun hora(fecha: Long): String = SimpleDateFormat("HH:mm", local).format(Date(fecha))

    /** Fecha completa: "18 oct. 2026, 10:32" */
    fun fechaHora(fecha: Long): String = SimpleDateFormat("d MMM yyyy, HH:mm", local).format(Date(fecha))
}
