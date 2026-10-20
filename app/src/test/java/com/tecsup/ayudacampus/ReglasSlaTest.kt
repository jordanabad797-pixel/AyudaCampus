package com.tecsup.ayudacampus

import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.NivelSla
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.model.ReglasSla
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglasSlaTest {

    private val hora = 3_600_000L
    private val creado = 1_000_000_000L

    @Test
    fun plazosPorPrioridad() {
        assertEquals(4, ReglasSla.plazoHoras(Prioridad.ALTA))
        assertEquals(24, ReglasSla.plazoHoras(Prioridad.MEDIA))
        assertEquals(72, ReglasSla.plazoHoras(Prioridad.BAJA))
    }

    @Test
    fun altaRecienCreadaEstaEnTiempo() {
        val sla = ReglasSla.calcular(creado, Prioridad.ALTA, EstadoTicket.ABIERTO, creado + hora)!!
        assertEquals(NivelSla.EN_TIEMPO, sla.nivel)
        assertEquals(3 * hora, sla.restanteMs)
    }

    @Test
    fun altaConMenosDel25PorCientoEstaPorVencer() {
        val sla = ReglasSla.calcular(creado, Prioridad.ALTA, EstadoTicket.EN_PROCESO, creado + 3 * hora + 30 * 60_000L)!!
        assertEquals(NivelSla.POR_VENCER, sla.nivel)
    }

    @Test
    fun altaPasadasCuatroHorasEstaVencida() {
        val sla = ReglasSla.calcular(creado, Prioridad.ALTA, EstadoTicket.ABIERTO, creado + 5 * hora)!!
        assertTrue(sla.vencido)
        assertTrue(sla.restanteMs < 0)
    }

    @Test
    fun justoEnElLimiteCuentaComoVencido() {
        val sla = ReglasSla.calcular(creado, Prioridad.MEDIA, EstadoTicket.ABIERTO, creado + 24 * hora)!!
        assertTrue(sla.vencido)
    }

    @Test
    fun bajaConDosDiasAunEstaEnTiempo() {
        val sla = ReglasSla.calcular(creado, Prioridad.BAJA, EstadoTicket.ABIERTO, creado + 48 * hora)!!
        assertEquals(NivelSla.EN_TIEMPO, sla.nivel)
        assertEquals(24 * hora, sla.restanteMs)
    }

    @Test
    fun resueltosYCerradosNoTienenSla() {
        assertNull(ReglasSla.calcular(creado, Prioridad.ALTA, EstadoTicket.RESUELTO, creado + 10 * hora))
        assertNull(ReglasSla.calcular(creado, Prioridad.ALTA, EstadoTicket.CERRADO, creado + 10 * hora))
    }
}
