package com.tecsup.ayudacampus.data.local

import androidx.room.withTransaction
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.local.entity.Comentario
import com.tecsup.ayudacampus.data.local.entity.Ticket
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.model.Rol

/**
 * [GUIADO · Paso 3] Datos de prueba ficticios (Ley 29733) que se cargan SOLO la primera vez.
 *
 * Carga: 5 categorías, 2 usuarios (estudiante y soporte), 6 tickets en distintos
 * estados y prioridades, y 4 comentarios, para que el panel no aparezca vacío.
 */
object DatosIniciales {

    private const val HORA = 3_600_000L // 1 hora en milisegundos

    suspend fun sembrar(db: AyudaDatabase) {
        // Si ya hay usuarios, la base ya fue sembrada: no hacemos nada.
        if (db.usuarioDao().contar() > 0) return

        // withTransaction: o se inserta todo, o no se inserta nada.
        db.withTransaction {
            val ahora = System.currentTimeMillis()
            val categoriaDao = db.categoriaDao()
            val usuarioDao = db.usuarioDao()
            val ticketDao = db.ticketDao()

            // ---------- 1. Categorías (guardamos los id generados) ----------
            val hardware = categoriaDao.insertar(Categoria(nombre = "Hardware", icono = "pc")).toInt()
            val software = categoriaDao.insertar(Categoria(nombre = "Software", icono = "code")).toInt()
            val red = categoriaDao.insertar(Categoria(nombre = "Red / Wi-Fi", icono = "wifi")).toInt()
            val cuenta = categoriaDao.insertar(Categoria(nombre = "Cuenta", icono = "key")).toInt()
            val impresion = categoriaDao.insertar(Categoria(nombre = "Impresión", icono = "printer")).toInt()

            // ---------- 2. Usuarios ----------
            usuarioDao.insertarTodos(
                listOf(
                    Usuario(nombre = "Juan Pérez", correo = "juan@campus.test", clave = "1234", rol = Rol.CLIENTE.name),
                    Usuario(nombre = "Soporte TI", correo = "soporte@campus.test", clave = "admin", rol = Rol.ADMIN.name)
                )
            )
            val juan = requireNotNull(usuarioDao.login("juan@campus.test", "1234"))
            val soporte = requireNotNull(usuarioDao.login("soporte@campus.test", "admin"))

            // ---------- 3. Tickets (del más antiguo al más reciente) ----------
            ticketDao.insertarTicket(
                Ticket(
                    clienteId = juan.id, categoriaId = cuenta,
                    titulo = "Olvidé mi contraseña del aula virtual",
                    descripcion = "No puedo ingresar al aula virtual desde ayer, me dice clave incorrecta.",
                    ubicacion = "Remoto",
                    prioridad = Prioridad.BAJA.name, estado = EstadoTicket.CERRADO.name,
                    fecha = ahora - 120 * HORA, fechaCierre = ahora - 96 * HORA, calificacion = 5
                )
            )
            val impresora = ticketDao.insertarTicket(
                Ticket(
                    clienteId = juan.id, categoriaId = impresion,
                    titulo = "No puedo imprimir en biblioteca",
                    descripcion = "La impresora del segundo piso no responde cuando envío documentos.",
                    ubicacion = "Biblioteca",
                    prioridad = Prioridad.BAJA.name, estado = EstadoTicket.RESUELTO.name,
                    fecha = ahora - 72 * HORA
                )
            ).toInt()
            ticketDao.insertarTicket(
                Ticket(
                    clienteId = juan.id, categoriaId = hardware,
                    titulo = "Teclado no responde",
                    descripcion = "El teclado de la PC 12 no escribe algunas letras.",
                    ubicacion = "Aula 204",
                    prioridad = Prioridad.MEDIA.name, estado = EstadoTicket.ABIERTO.name,
                    fecha = ahora - 26 * HORA
                )
            )
            ticketDao.insertarTicket(
                Ticket(
                    clienteId = juan.id, categoriaId = software,
                    titulo = "Instalar Android Studio",
                    descripcion = "Las PCs del laboratorio 2 no tienen Android Studio para la clase de móviles.",
                    ubicacion = "Lab 2",
                    prioridad = Prioridad.MEDIA.name, estado = EstadoTicket.EN_PROCESO.name,
                    fecha = ahora - 5 * HORA
                )
            )
            val wifi = ticketDao.insertarTicket(
                Ticket(
                    clienteId = juan.id, categoriaId = red,
                    titulo = "Sin Wi-Fi en el laboratorio 3",
                    descripcion = "Desde ayer no hay señal de Wi-Fi en todo el laboratorio. Afecta a 30 alumnos durante la clase.",
                    ubicacion = "Lab 3 - Pabellón B",
                    prioridad = Prioridad.ALTA.name, estado = EstadoTicket.EN_PROCESO.name,
                    fecha = ahora - 2 * HORA
                )
            ).toInt()
            // ALTA + ABIERTO con más de 1 hora → aparece en la alerta del panel.
            ticketDao.insertarTicket(
                Ticket(
                    clienteId = juan.id, categoriaId = hardware,
                    titulo = "Proyector del aula 305 no enciende",
                    descripcion = "El proyector no prende y hay exposición a las 3 pm.",
                    ubicacion = "Aula 305",
                    prioridad = Prioridad.ALTA.name, estado = EstadoTicket.ABIERTO.name,
                    fecha = ahora - 90 * 60_000L
                )
            )

            // ---------- 4. Comentarios (autorNombre y autorRol: "foto" del momento) ----------
            ticketDao.insertarComentario(
                Comentario(
                    ticketId = wifi, autorId = soporte.id, autorNombre = soporte.nombre, autorRol = soporte.rol,
                    texto = "Revisaremos el access point del pabellón B hoy.", fecha = ahora - 100 * 60_000L
                )
            )
            ticketDao.insertarComentario(
                Comentario(
                    ticketId = wifi, autorId = juan.id, autorNombre = juan.nombre, autorRol = juan.rol,
                    texto = "Gracias, quedo atento.", fecha = ahora - 90 * 60_000L
                )
            )
            ticketDao.insertarComentario(
                Comentario(
                    ticketId = wifi, autorId = soporte.id, autorNombre = soporte.nombre, autorRol = soporte.rol,
                    texto = "Reiniciamos el equipo. ¿Ya tienes señal?", fecha = ahora - 30 * 60_000L
                )
            )
            ticketDao.insertarComentario(
                Comentario(
                    ticketId = impresora, autorId = soporte.id, autorNombre = soporte.nombre, autorRol = soporte.rol,
                    texto = "Reinstalamos el driver de la impresora. Ya debería imprimir.", fecha = ahora - 48 * HORA
                )
            )
        }
    }
}
