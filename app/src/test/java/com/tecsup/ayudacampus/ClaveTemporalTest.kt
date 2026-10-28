package com.tecsup.ayudacampus

import com.tecsup.ayudacampus.model.ClaveTemporal
import com.tecsup.ayudacampus.model.SolicitudClave
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ClaveTemporalTest {

    @Test
    fun tieneLongitudLetrasYNumeros() {
        repeat(200) { semilla ->
            val clave = ClaveTemporal.generar(Random(semilla))
            assertEquals(ClaveTemporal.LONGITUD, clave.length)
            assertTrue(clave.any { it.isLetter() })
            assertTrue(clave.any { it.isDigit() })
        }
    }

    @Test
    fun noUsaCaracteresConfusos() {
        repeat(200) { semilla ->
            val clave = ClaveTemporal.generar(Random(semilla))
            assertFalse(clave.any { it in "0O1lI" })
        }
    }

    @Test
    fun soloLaCategoriaCuentaPermiteRestablecer() {
        assertTrue(SolicitudClave.esDeCuenta("Cuenta"))
        assertTrue(SolicitudClave.esDeCuenta(" cuenta "))
        assertFalse(SolicitudClave.esDeCuenta("Hardware"))
    }
}
