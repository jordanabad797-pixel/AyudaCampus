package com.tecsup.ayudacampus

import com.tecsup.ayudacampus.model.FuerzaClave
import com.tecsup.ayudacampus.model.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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

    @Test
    fun nuevaClaveExigeLongitudLetrasYNumeros() {
        assertNotNull(Validaciones.errorNuevaClave("abc12"))
        assertNotNull(Validaciones.errorNuevaClave("solamenteletras"))
        assertNotNull(Validaciones.errorNuevaClave("12345678"))
        assertNotNull(Validaciones.errorNuevaClave("clave con 1 espacio"))
        assertNull(Validaciones.errorNuevaClave("Campus2026"))
    }

    @Test
    fun nuevaClaveNoPuedeContenerNombreNiCorreo() {
        val datos = listOf("Juan Pérez", "jperez@campus.test")
        assertNotNull(Validaciones.errorNuevaClave("juan2026x", datos))
        assertNotNull(Validaciones.errorNuevaClave("Jperez123", datos))
        assertNull(Validaciones.errorNuevaClave("Laptop2026", datos))
    }

    @Test
    fun fuerzaDeLaClave() {
        assertEquals(FuerzaClave.DEBIL, Validaciones.fuerzaClave("abc"))
        assertEquals(FuerzaClave.MEDIA, Validaciones.fuerzaClave("campus2026"))
        assertEquals(FuerzaClave.FUERTE, Validaciones.fuerzaClave("Campus#2026"))
    }

    @Test
    fun nombreSoloLetrasYConApellido() {
        assertNull(Validaciones.errorNombre("María José Núñez"))
        assertNull(Validaciones.errorNombre("  Ana   O'Neill "))
        assertNotNull(Validaciones.errorNombre("Juan"))
        assertNotNull(Validaciones.errorNombre("Juan 123"))
        assertNotNull(Validaciones.errorNombre("Juan_Pérez"))
    }

    @Test
    fun registroExigeCorreoInstitucional() {
        assertNull(Validaciones.errorCorreoRegistro("Ana@Campus.test"))
        assertNotNull(Validaciones.errorCorreoRegistro("ana@gmail.com"))
        assertNotNull(Validaciones.errorCorreoRegistro("ana@campus"))
    }
}
