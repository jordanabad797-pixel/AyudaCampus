package com.tecsup.ayudacampus

import com.tecsup.ayudacampus.model.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {

    @Test
    fun correosValidos() {
        assertTrue(Validaciones.esCorreoValido("juan@campus.test"))
        assertTrue(Validaciones.esCorreoValido("ana.torres+lab@tecsup.edu.pe"))
        assertTrue(Validaciones.esCorreoValido("  maria@campus.test  "))
    }

    @Test
    fun correosInvalidos() {
        assertFalse(Validaciones.esCorreoValido(""))
        assertFalse(Validaciones.esCorreoValido("juan"))
        assertFalse(Validaciones.esCorreoValido("juan@"))
        assertFalse(Validaciones.esCorreoValido("juan@campus"))
        assertFalse(Validaciones.esCorreoValido("juan perez@campus.test"))
        assertFalse(Validaciones.esCorreoValido("juan@@campus.test"))
    }

    @Test
    fun normalizarCorreoQuitaEspaciosYMayusculas() {
        assertEquals("juan@campus.test", Validaciones.normalizarCorreo("  Juan@Campus.TEST "))
    }
}
