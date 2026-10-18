package com.tecsup.ayudacampus.model

/**
 * [ENTREGADO] Ciclo de vida de un ticket. El orden de declaración ES el orden del stepper:
 * ABIERTO → EN_PROCESO → RESUELTO → CERRADO. En la tabla se guarda [name].
 */
enum class EstadoTicket(val etiqueta: String) {
    ABIERTO("Abierto"),
    EN_PROCESO("En proceso"),
    RESUELTO("Resuelto"),
    CERRADO("Cerrado");

    companion object {
        /** Lee el texto de la base; si no es válido, asume ABIERTO. */
        fun desde(valor: String): EstadoTicket = entries.firstOrNull { it.name == valor } ?: ABIERTO
    }
}
