package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.data.local.entity.estadoEnum
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.local.relation.conversacion
import com.tecsup.ayudacampus.model.EstadoSla
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.BarraMensaje
import com.tecsup.ayudacampus.ui.components.BurbujaComentario
import com.tecsup.ayudacampus.ui.components.Estrellas
import com.tecsup.ayudacampus.ui.components.Formatos
import com.tecsup.ayudacampus.ui.components.IconoCategoria
import com.tecsup.ayudacampus.ui.components.SlaChip
import com.tecsup.ayudacampus.ui.theme.color
import com.tecsup.ayudacampus.viewmodel.AtenderViewModel

@Composable
fun AtenderTicketScreen(
    vm: AtenderViewModel,
    ticketId: Int,
    onAtras: () -> Unit
) {
    val detalle by vm.ticket.collectAsStateWithLifecycle()
    val estado by vm.estado.collectAsStateWithLifecycle()
    val prioridad by vm.prioridad.collectAsStateWithLifecycle()
    val hayCambios by vm.hayCambios.collectAsStateWithLifecycle()
    val respuesta by vm.respuesta.collectAsStateWithLifecycle()
    val procesando by vm.procesando.collectAsStateWithLifecycle()
    val sla by vm.sla.collectAsStateWithLifecycle()

    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        vm.mensaje.collect { snackbar.showSnackbar(it) }
    }

    val cerrado = detalle?.ticket?.estadoEnum == EstadoTicket.CERRADO

    Scaffold(
        topBar = { AppTopBar(titulo = "Atender ticket ${Formatos.numero(ticketId)}", onAtras = onAtras) },
        bottomBar = {
            if (detalle != null && !cerrado) {
                BarraMensaje(
                    texto = respuesta,
                    onTexto = vm::escribir,
                    onEnviar = { vm.responder() },
                    placeholder = "Responder al cliente…",
                    enabled = !procesando
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val t = detalle
        if (t == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { DatosTicket(t, sla) }

            item { Seccion("Estado") }
            item {
                SelectorEstado(
                    seleccionado = estado,
                    bloqueado = cerrado || procesando,
                    onElegir = vm::cambiarEstado
                )
            }

            item { Seccion("Prioridad") }
            item {
                SelectorPrioridad(
                    seleccionada = prioridad,
                    enabled = !cerrado && !procesando,
                    onElegir = vm::cambiarPrioridad
                )
            }

            if (cerrado) {
                item { AvisoCerrado(calificacion = t.ticket.calificacion ?: 0) }
            } else {
                item {
                    AyudaButton(
                        texto = "Guardar cambios",
                        onClick = vm::guardar,
                        enabled = hayCambios,
                        cargando = procesando && hayCambios,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            item { Seccion("Conversación") }
            if (t.conversacion.isEmpty()) {
                item {
                    Text(
                        "Aún no hay mensajes. Escribe una respuesta para el cliente.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(t.conversacion, key = { it.id }) { c ->
                BurbujaComentario(
                    autor = c.autorNombre,
                    texto = c.texto,
                    fecha = c.fecha,
                    esPropio = c.autorId == vm.soporteId
                )
            }
        }
    }
}

@Composable
private fun Seccion(titulo: String) {
    Text(titulo, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 6.dp))
}

@Composable
private fun DatosTicket(detalle: TicketConDetalle, sla: EstadoSla?) {
    val t = detalle.ticket
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(t.titulo, style = MaterialTheme.typography.titleMedium)
            if (sla != null) {
                Spacer(Modifier.height(8.dp))
                SlaChip(sla)
            }
            Spacer(Modifier.height(8.dp))
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
            Spacer(Modifier.height(12.dp))
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(detalle.cliente.nombre, style = MaterialTheme.typography.labelLarge)
                        Text(detalle.cliente.correo, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorEstado(
    seleccionado: EstadoTicket?,
    bloqueado: Boolean,
    onElegir: (EstadoTicket) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EstadoTicket.entries.forEach { e ->
            val activo = e == seleccionado
            FilterChip(
                selected = activo,
                onClick = { onElegir(e) },
                enabled = !bloqueado && e != EstadoTicket.CERRADO,
                label = { Text(e.etiqueta) },
                leadingIcon = if (e == EstadoTicket.CERRADO) {
                    { Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                shape = MaterialTheme.shapes.extraLarge,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                    disabledSelectedContainerColor = e.color().copy(alpha = 0.25f)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = !bloqueado && e != EstadoTicket.CERRADO,
                    selected = activo,
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun SelectorPrioridad(seleccionada: Prioridad?, enabled: Boolean, onElegir: (Prioridad) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Prioridad.entries.forEach { p ->
            val activa = p == seleccionada
            val fondo by animateColorAsState(
                if (activa) p.color() else MaterialTheme.colorScheme.surface,
                label = "fondoPrioridad"
            )
            Surface(
                onClick = { onElegir(p) },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp),
                shape = MaterialTheme.shapes.small,
                color = fondo,
                contentColor = if (activa) Color.White else MaterialTheme.colorScheme.onSurface,
                border = if (activa) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(
                    p.etiqueta,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun AvisoCerrado(calificacion: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Ticket cerrado por el estudiante",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(8.dp))
            Estrellas(calificacion = calificacion, tamano = 28.dp)
        }
    }
}
