package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.data.local.entity.estadoEnum
import com.tecsup.ayudacampus.data.local.entity.prioridadEnum
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.local.relation.conversacion
import com.tecsup.ayudacampus.model.EstadoSla
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.BarraMensaje
import com.tecsup.ayudacampus.ui.components.BurbujaComentario
import com.tecsup.ayudacampus.ui.components.EstadoChip
import com.tecsup.ayudacampus.ui.components.EstadoVacio
import com.tecsup.ayudacampus.ui.components.Estrellas
import com.tecsup.ayudacampus.ui.components.Formatos
import com.tecsup.ayudacampus.ui.components.IconoCategoria
import com.tecsup.ayudacampus.ui.components.PrioridadChip
import com.tecsup.ayudacampus.ui.components.SlaChip
import com.tecsup.ayudacampus.ui.components.StepperTicket
import com.tecsup.ayudacampus.ui.components.VarianteBoton
import com.tecsup.ayudacampus.ui.theme.Exito
import com.tecsup.ayudacampus.viewmodel.TicketDetalleViewModel

@Composable
fun DetalleTicketScreen(
    vm: TicketDetalleViewModel,
    ticketId: Int,
    onAtras: () -> Unit
) {
    val detalle by vm.ticket.collectAsStateWithLifecycle()
    val noEncontrado by vm.noEncontrado.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val calificacion by vm.calificacion.collectAsStateWithLifecycle()
    val comentarioFinal by vm.comentarioFinal.collectAsStateWithLifecycle()
    val procesando by vm.procesando.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val sla by vm.sla.collectAsStateWithLifecycle()

    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(error) {
        error?.let {
            snackbar.showSnackbar(it)
            vm.limpiarError()
        }
    }

    val estado = detalle?.ticket?.estadoEnum

    Scaffold(
        topBar = { AppTopBar(titulo = "Ticket ${Formatos.numero(ticketId)}", onAtras = onAtras) },
        bottomBar = {
            if (estado != null && estado != EstadoTicket.CERRADO) {
                BarraMensaje(
                    texto = mensaje,
                    onTexto = vm::escribir,
                    onEnviar = { vm.comentar() },
                    placeholder = "Escribe un comentario…",
                    enabled = !procesando
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val t = detalle
        when {
            noEncontrado -> EstadoVacio(
                icono = Icons.Outlined.SearchOff,
                titulo = "Ticket no disponible",
                detalle = "No existe o no pertenece a tu cuenta.",
                modifier = Modifier.padding(padding)
            )

            t == null -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            else -> ContenidoDetalle(
                detalle = t,
                usuarioId = vm.usuarioId,
                calificacion = calificacion,
                comentarioFinal = comentarioFinal,
                procesando = procesando,
                onCalificar = vm::calificar,
                onComentarioFinal = vm::escribirComentarioFinal,
                onCerrar = { vm.cerrar() },
                onReabrir = vm::reabrir,
                contentPadding = padding,
                sla = sla
            )
        }
    }
}

@Composable
private fun ContenidoDetalle(
    detalle: TicketConDetalle,
    usuarioId: Int,
    calificacion: Int,
    comentarioFinal: String,
    procesando: Boolean,
    onCalificar: (Int) -> Unit,
    onComentarioFinal: (String) -> Unit,
    onCerrar: () -> Unit,
    onReabrir: () -> Unit,
    contentPadding: PaddingValues,
    sla: EstadoSla?
) {
    val estado = detalle.ticket.estadoEnum
    val conversacion = detalle.conversacion
    val lista = rememberLazyListState()

    var mensajesPrevios by remember { mutableIntStateOf(conversacion.size) }
    LaunchedEffect(conversacion.size) {
        if (conversacion.size > mensajesPrevios) {
            lista.animateScrollToItem(INDICE_PRIMER_MENSAJE + conversacion.size - 1)
        }
        mensajesPrevios = conversacion.size
    }

    LazyColumn(
        state = lista,
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { CabeceraTicket(detalle, sla) }
        item {
            StepperTicket(estado = estado, modifier = Modifier.padding(vertical = 8.dp))
        }
        item {
            Text(
                "Conversación",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        if (conversacion.isEmpty()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Forum,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Aún no hay mensajes. Soporte TI te responderá aquí.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        items(conversacion, key = { it.id }) { c ->
            BurbujaComentario(
                autor = c.autorNombre,
                texto = c.texto,
                fecha = c.fecha,
                esPropio = c.autorId == usuarioId
            )
        }

        when (estado) {
            EstadoTicket.RESUELTO -> item {
                SeccionResuelto(
                    calificacion = calificacion,
                    comentarioFinal = comentarioFinal,
                    procesando = procesando,
                    onCalificar = onCalificar,
                    onComentarioFinal = onComentarioFinal,
                    onCerrar = onCerrar,
                    onReabrir = onReabrir
                )
            }

            EstadoTicket.CERRADO -> item {
                SeccionCerrado(
                    calificacion = detalle.ticket.calificacion ?: 0,
                    fechaCierre = detalle.ticket.fechaCierre
                )
            }

            else -> Unit
        }
    }
}

private const val INDICE_PRIMER_MENSAJE = 3

@Composable
private fun CabeceraTicket(detalle: TicketConDetalle, sla: EstadoSla?) {
    val t = detalle.ticket
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(t.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                EstadoChip(t.estadoEnum)
                PrioridadChip(t.prioridadEnum)
                if (sla != null) SlaChip(sla)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoCategoria(clave = detalle.categoria.icono, tamano = 24.dp)
                Spacer(Modifier.width(8.dp))
                Text(
                    listOf(detalle.categoria.nombre, t.ubicacion, Formatos.haceCuanto(t.fecha))
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(t.descripcion, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SeccionResuelto(
    calificacion: Int,
    comentarioFinal: String,
    procesando: Boolean,
    onCalificar: (Int) -> Unit,
    onComentarioFinal: (String) -> Unit,
    onCerrar: () -> Unit,
    onReabrir: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
        Surface(
            color = Exito.copy(alpha = 0.12f),
            contentColor = Exito,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(10.dp))
                Text(
                    "Soporte marcó este ticket como resuelto. ¿El problema se solucionó?",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        TarjetaCalificacion(titulo = "Califica la atención", calificacion = calificacion, onCalificar = onCalificar)

        AyudaTextField(
            valor = comentarioFinal,
            onValorChange = onComentarioFinal,
            etiqueta = "Comentario final (opcional)",
            singleLine = false,
            minLines = 2,
            maxLength = 300,
            enabled = !procesando
        )

        AyudaButton(
            texto = "Confirmar y cerrar",
            onClick = onCerrar,
            enabled = calificacion in 1..5,
            cargando = procesando
        )
        AyudaButton(
            texto = "Reabrir ticket",
            onClick = onReabrir,
            variante = VarianteBoton.PELIGRO,
            enabled = !procesando
        )
    }
}

@Composable
private fun SeccionCerrado(calificacion: Int, fechaCierre: Long?) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
        TarjetaCalificacion(titulo = "Tu calificación", calificacion = calificacion, onCalificar = null)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Ticket cerrado" + (fechaCierre?.let { " el ${Formatos.fechaHora(it)}" } ?: ""),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TarjetaCalificacion(titulo: String, calificacion: Int, onCalificar: ((Int) -> Unit)?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(titulo, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(10.dp))
            Estrellas(calificacion = calificacion, onCambio = onCalificar)
            Spacer(Modifier.height(6.dp))
            Text(
                if (calificacion == 0) "Toca una estrella" else "$calificacion de 5 estrellas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
