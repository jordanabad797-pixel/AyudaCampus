package com.tecsup.ayudacampus.model

/**
 * Bloqueo temporal por intentos fallidos de inicio de sesión (por correo).
 * Tras [maxIntentos] fallos seguidos, ese correo queda bloqueado [bloqueoMs] milisegundos.
 * También cuenta correos que no existen: así no se puede adivinar qué cuentas hay.
 */
class ControlIntentos(
    private val maxIntentos: Int = MAX_INTENTOS,
    private val bloqueoMs: Long = BLOQUEO_MS
) {
    private val fallos = mutableMapOf<String, Int>()
    private val bloqueadoHasta = mutableMapOf<String, Long>()

    fun restanteMs(correo: String, ahora: Long): Long =
        ((bloqueadoHasta[correo] ?: 0L) - ahora).coerceAtLeast(0L)

    /** Registra un fallo y devuelve los intentos que quedan (0 = acaba de bloquearse). */
    fun registrarFallo(correo: String, ahora: Long): Int {
        val total = (fallos[correo] ?: 0) + 1
        return if (total >= maxIntentos) {
            fallos.remove(correo)
            bloqueadoHasta[correo] = ahora + bloqueoMs
            0
        } else {
            fallos[correo] = total
            maxIntentos - total
        }
    }

    fun reiniciar(correo: String) {
        fallos.remove(correo)
        bloqueadoHasta.remove(correo)
    }

    companion object {
        const val MAX_INTENTOS = 5
        const val BLOQUEO_MS = 30_000L
    }
}
