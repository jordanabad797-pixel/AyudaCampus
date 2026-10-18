package com.tecsup.ayudacampus.data.local

import com.tecsup.ayudacampus.data.local.entity.Categoria

/**
 * [GUIADO] Datos de prueba ficticios (Ley 29733) que se cargan solo la primera vez.
 */
object DatosIniciales {

    suspend fun sembrar(db: AyudaDatabase) {
        // TODO (Paso 3): la condición será `db.usuarioDao().contar() == 0`
        if (db.categoriaDao().contar() > 0) return

        db.categoriaDao().insertarTodas(categorias())

        // TODO (Paso 3): insertar 1 estudiante (juan@campus.test / 1234),
        //  1 soporte (soporte@campus.test / admin), 6 tickets en distintos estados
        //  y prioridades, y 3 o 4 comentarios.
    }

    private fun categorias() = listOf(
        Categoria(nombre = "Hardware", icono = "pc"),
        Categoria(nombre = "Software", icono = "code"),
        Categoria(nombre = "Red / Wi-Fi", icono = "wifi"),
        Categoria(nombre = "Cuenta", icono = "key"),
        Categoria(nombre = "Impresión", icono = "printer")
    )
}
