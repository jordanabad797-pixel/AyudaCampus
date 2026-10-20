package com.tecsup.ayudacampus.model

enum class NivelSla { EN_TIEMPO, POR_VENCER, VENCIDO }

/** Resultado del SLA de un ticket en un instante dado. */
data class EstadoSla(
    val restanteMs: Long,
    val plazoHoras: Int,
    val nivel: NivelSla
) {
    val vencido: Boolean get() = nivel == NivelSla.VENCIDO
}

/**
 * Plazos de atención por prioridad: Alta 4 h, Media 24 h, Baja 72 h, contados desde la creación.
 * Un ticket RESUELTO o CERRADO ya no corre SLA (devuelve null).
 * "Por vencer" = queda el 25 % del plazo o menos.
 */
object ReglasSla {

    private const val HORA_MS = 3_600_000L
    private const val UMBRAL_POR_VENCER = 0.25

    fun plazoHoras(prioridad: Prioridad): Int = when (prioridad) {
        Prioridad.ALTA -> 4
        Prioridad.MEDIA -> 24
        Prioridad.BAJA -> 72
    }

    fun calcular(fecha: Long, prioridad: Prioridad, estado: EstadoTicket, ahora: Long): EstadoSla? {
        if (estado == EstadoTicket.RESUELTO || estado == EstadoTicket.CERRADO) return null

        val plazo = plazoHoras(prioridad)
        val plazoMs = plazo * HORA_MS
        val restante = fecha + plazoMs - ahora
        val nivel = when {
            restante <= 0L -> NivelSla.VENCIDO
            restante <= plazoMs * UMBRAL_POR_VENCER -> NivelSla.POR_VENCER
            else -> NivelSla.EN_TIEMPO
        }
        return EstadoSla(restanteMs = restante, plazoHoras = plazo, nivel = nivel)
    }
}
