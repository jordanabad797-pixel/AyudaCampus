package com.tecsup.ayudacampus.model

/**
 * [ENTREGADO] Perfil del usuario. En Room y DataStore se guarda como texto ([name]).
 * CLIENTE = Estudiante · ADMIN = Soporte TI.
 */
enum class Rol(val etiqueta: String) {
    CLIENTE("Estudiante"),
    ADMIN("Soporte TI");

    /** El otro perfil (para el botón "Cambiar de perfil" en modo práctica). */
    fun otro(): Rol = if (this == CLIENTE) ADMIN else CLIENTE

    companion object {
        /** Convierte el texto guardado en un Rol; devuelve null si está vacío o es inválido. */
        fun desde(valor: String?): Rol? = entries.firstOrNull { it.name == valor }
    }
}
