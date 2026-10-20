package com.tecsup.ayudacampus.model

/** Reglas de formato compartidas por Login y Registro. */
object Validaciones {

    const val MIN_NOMBRE = 3
    const val MAX_NOMBRE = 50
    const val MIN_CLAVE = 4

    private val FORMATO_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun esCorreoValido(correo: String): Boolean = FORMATO_CORREO.matches(correo.trim())

    /** El correo se guarda siempre igual: sin espacios y en minúsculas. */
    fun normalizarCorreo(correo: String): String = correo.trim().lowercase()
}
