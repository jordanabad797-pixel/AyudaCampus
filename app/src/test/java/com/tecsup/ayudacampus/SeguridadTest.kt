package com.tecsup.ayudacampus

import com.tecsup.ayudacampus.model.ControlIntentos
import com.tecsup.ayudacampus.model.HashClave
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeguridadTest {

    @Test
    fun hashVerificaSoloLaClaveCorrecta() {
        val guardado = HashClave.generar("Campus2026")
        assertTrue(HashClave.verificar("Campus2026", guardado))
        assertFalse(HashClave.verificar("campus2026", guardado))
        assertFalse(guardado.contains("Campus2026"))
    }

    @Test
    fun mismaClaveProduceHashDistintoPorLaSal() {
        assertNotEquals(HashClave.generar("1234"), HashClave.generar("1234"))
    }

    @Test
    fun hashMalFormadoNoValida() {
        assertFalse(HashClave.verificar("1234", "1234"))
        assertFalse(HashClave.verificar("1234", "zz:zz"))
        assertFalse(HashClave.verificar("1234", ""))
    }

    @Test
    fun bloqueaTrasCincoFallosYLuegoLibera() {
        val control = ControlIntentos(maxIntentos = 5, bloqueoMs = 30_000L)
        repeat(4) { i -> assertEquals(4 - i, control.registrarFallo("juan@campus.test", 0L)) }
        assertEquals(0, control.registrarFallo("juan@campus.test", 0L))
        assertEquals(30_000L, control.restanteMs("juan@campus.test", 0L))
        assertEquals(10_000L, control.restanteMs("juan@campus.test", 20_000L))
        assertEquals(0L, control.restanteMs("juan@campus.test", 30_000L))
        assertEquals(0L, control.restanteMs("otro@campus.test", 0L))
    }

    @Test
    fun unLoginCorrectoReiniciaLosFallos() {
        val control = ControlIntentos(maxIntentos = 3, bloqueoMs = 1_000L)
        control.registrarFallo("a@campus.test", 0L)
        control.registrarFallo("a@campus.test", 0L)
        control.reiniciar("a@campus.test")
        assertEquals(2, control.registrarFallo("a@campus.test", 0L))
    }
}
