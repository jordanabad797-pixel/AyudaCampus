package com.tecsup.ayudacampus.model

/**
 * [ENTREGADO] Prioridad de un ticket. En la tabla se guarda [name].
 * El orden de la bandeja (ALTA, MEDIA, BAJA) se resuelve en SQL con CASE, no aquí.
 */
enum class Prioridad(val etiqueta: String) {
    BAJA("Baja"),
    MEDIA("Media"),
    ALTA("Alta");

    companion object {
        /** Lee el texto de la base; si no es válido, asume MEDIA (la prioridad por defecto). */
        fun desde(valor: String): Prioridad = entries.firstOrNull { it.name == valor } ?: MEDIA
    }
}
