package com.tecsup.ayudacampus

import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.local.entity.Comentario
import com.tecsup.ayudacampus.data.local.entity.Ticket
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.viewmodel.sinNotasInternas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NotasInternasTest {

    private fun comentario(id: Int, interno: Boolean) = Comentario(
        id = id, ticketId = 1, autorId = 2, autorNombre = "Soporte TI", autorRol = "ADMIN",
        texto = "mensaje $id", fecha = id.toLong(), interno = interno
    )

    private val detalle = TicketConDetalle(
        ticket = Ticket(id = 1, clienteId = 1, categoriaId = 1, titulo = "Sin Wi-Fi", descripcion = "No hay señal", ubicacion = "Lab 3"),
        comentarios = listOf(comentario(1, false), comentario(2, true), comentario(3, false)),
        categoria = Categoria(id = 1, nombre = "Red / Wi-Fi", icono = "wifi"),
        cliente = Usuario(id = 1, nombre = "Juan Pérez", correo = "juan@campus.test", clave = "1234")
    )

    @Test
    fun elEstudianteNoRecibeNotasInternas() {
        val visto = detalle.sinNotasInternas()
        assertEquals(listOf(1, 3), visto.comentarios.map { it.id })
        assertTrue(visto.comentarios.none { it.interno })
    }

    @Test
    fun elRestoDelTicketNoCambia() {
        val visto = detalle.sinNotasInternas()
        assertEquals(detalle.ticket, visto.ticket)
        assertEquals(detalle.cliente, visto.cliente)
    }
}
