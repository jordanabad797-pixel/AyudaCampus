package com.tecsup.ayudacampus.model

/** Ticket que se crea automáticamente desde "¿Olvidaste tu contraseña?". */
object SolicitudClave {

    const val TITULO = "Restablecer contraseña"
    const val CATEGORIA = "Cuenta"
    const val UBICACION = "Remoto"
    const val DESCRIPCION =
        "Solicitud generada desde «¿Olvidaste tu contraseña?». " +
            "El estudiante no puede ingresar a su cuenta y pide restablecer su contraseña."

    /** La acción de restablecer contraseña solo aplica a tickets de la categoría Cuenta. */
    fun esDeCuenta(nombreCategoria: String): Boolean = nombreCategoria.trim().equals(CATEGORIA, ignoreCase = true)
}
