package com.tecsup.ayudacampus.model

enum class FuerzaClave(val etiqueta: String) { DEBIL("Débil"), MEDIA("Media"), FUERTE("Fuerte") }

/** Reglas de formato compartidas por Login, Registro y Cambiar contraseña. */
object Validaciones {

    const val MIN_NOMBRE = 3
    const val MAX_NOMBRE = 50
    const val MIN_CLAVE = 8
    const val MAX_CLAVE = 64

    /** Solo se registran cuentas con el correo de la institución. */
    const val DOMINIO_INSTITUCIONAL = "campus.test"

    private val FORMATO_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    /** Letras (con tildes y ñ) separadas por un espacio, apóstrofo o guion: "María José O'Neill". */
    private val FORMATO_NOMBRE = Regex("^\\p{L}+(?:[ '\\-]\\p{L}+)*$")

    fun esCorreoValido(correo: String): Boolean = FORMATO_CORREO.matches(correo.trim())

    /** El correo se guarda siempre igual: sin espacios y en minúsculas. */
    fun normalizarCorreo(correo: String): String = correo.trim().lowercase()

    fun esCorreoInstitucional(correo: String): Boolean =
        normalizarCorreo(correo).endsWith("@$DOMINIO_INSTITUCIONAL")

    /** Quita espacios de más: "  juan   pérez " → "juan pérez". */
    fun normalizarNombre(nombre: String): String = nombre.trim().replace(Regex("\\s+"), " ")

    fun errorNombre(nombre: String): String? {
        val limpio = normalizarNombre(nombre)
        return when {
            limpio.length < MIN_NOMBRE -> "El nombre debe tener al menos $MIN_NOMBRE caracteres"
            limpio.length > MAX_NOMBRE -> "El nombre no puede pasar de $MAX_NOMBRE caracteres"
            !FORMATO_NOMBRE.matches(limpio) -> "Usa solo letras, sin números ni símbolos"
            limpio.split(" ").size < 2 -> "Escribe tu nombre y apellido"
            else -> null
        }
    }

    fun errorCorreoRegistro(correo: String): String? = when {
        correo.isBlank() -> "Ingresa tu correo"
        !esCorreoValido(correo) -> "El correo no tiene un formato válido"
        !esCorreoInstitucional(correo) -> "Usa tu correo institucional (@$DOMINIO_INSTITUCIONAL)"
        else -> null
    }

    /**
     * Regla única para toda contraseña nueva (registro y cambio de contraseña).
     * @param datosPersonales nombre y correo del usuario: la contraseña no debe contenerlos.
     */
    fun errorNuevaClave(clave: String, datosPersonales: List<String> = emptyList()): String? = when {
        clave.length < MIN_CLAVE -> "La contraseña debe tener al menos $MIN_CLAVE caracteres"
        clave.length > MAX_CLAVE -> "La contraseña no puede pasar de $MAX_CLAVE caracteres"
        clave.any { it.isWhitespace() } -> "La contraseña no puede tener espacios"
        clave.none { it.isLetter() } || clave.none { it.isDigit() } -> "Combina letras y números"
        contieneDatosPersonales(clave, datosPersonales) -> "No uses tu nombre ni tu correo en la contraseña"
        else -> null
    }

    /** Débil = no cumple el mínimo · Media = cumple · Fuerte = además usa mayúsculas, símbolos o 12+ caracteres. */
    fun fuerzaClave(clave: String): FuerzaClave {
        var puntos = 0
        if (clave.length >= MIN_CLAVE) puntos++
        if (clave.any { it.isLetter() } && clave.any { it.isDigit() }) puntos++
        if (clave.length >= 12) puntos++
        if (clave.any { it.isLowerCase() } && clave.any { it.isUpperCase() }) puntos++
        if (clave.any { !it.isLetterOrDigit() }) puntos++
        return when {
            puntos >= 4 -> FuerzaClave.FUERTE
            puntos >= 2 -> FuerzaClave.MEDIA
            else -> FuerzaClave.DEBIL
        }
    }

    /** Compara contra cada palabra del nombre y la parte del correo antes de "@" (de 3 letras o más). */
    private fun contieneDatosPersonales(clave: String, datos: List<String>): Boolean {
        val claveMin = clave.lowercase()
        return datos
            .flatMap { dato -> dato.lowercase().substringBefore("@").split(" ", ".", "_", "-") }
            .filter { it.length >= 3 }
            .any { claveMin.contains(it) }
    }
}
