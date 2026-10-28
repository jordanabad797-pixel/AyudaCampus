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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.Role
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
import com.tecsup.ayudacampus.ui.components.ConversacionVacia
import com.tecsup.ayudacampus.ui.components.Estrellas
import com.tecsup.ayudacampus.ui.components.Formatos
import com.tecsup.ayudacampus.ui.components.IconoCategoria
import com.tecsup.ayudacampus.ui.components.SlaChip
import com.tecsup.ayudacampus.ui.components.VarianteBoton
import com.tecsup.ayudacampus.ui.theme.EstadoAbierto
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
    val notaInterna by vm.notaInterna.collectAsStateWithLifecycle()
    val esSolicitudDeCuenta by vm.esSolicitudDeCuenta.collectAsStateWithLifecycle()
    val claveTemporal by vm.claveTemporal.collectAsStateWithLifecycle()
    var confirmarRestablecer by rememberSaveable { mutableStateOf(false) }

    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        vm.mensaje.collect { snackbar.showSnackbar(it) }
    }

    val cerrado = detalle?.ticket?.estadoEnum == EstadoTicket.CERRADO

    if (confirmarRestablecer) {
        AlertDialog(
            onDismissRequest = { confirmarRestablecer = false },
            icon = { Icon(Icons.Outlined.Key, contentDescription = null) },
            title = { Text("¿Restablecer contraseña?") },
            text = {
                Text(
                    "Se reemplazará la contraseña de ${detalle?.cliente?.nombre.orEmpty()} por una temporal " +
                        "y el ticket pasará a Resuelto. Verifica antes su identidad."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmarRestablecer = false
                    vm.restablecerClave()
                }) { Text("Restablecer") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarRestablecer = false }) { Text("Cancelar") }
            }
        )
    }

    claveTemporal?.let { clave ->
        DialogoClaveTemporal(clave = clave, onCerrar = vm::ocultarClaveTemporal)
    }

    Scaffold(
        topBar = { AppTopBar(titulo = "Atender ticket ${Formatos.numero(ticketId)}", onAtras = onAtras) },
        bottomBar = {
            if (detalle != null && !cerrado) {
                BarraMensaje(
                    texto = respuesta,
                    onTexto = vm::escribir,
                    onEnviar = { vm.responder() },
                    placeholder = if (notaInterna) "Escribe una nota interna…" else "Responder al cliente…",
                    enabled = !procesando,
                    encabezado = {
                        SelectorNotaInterna(activa = notaInterna, onCambio = vm::cambiarNotaInterna)
                    }
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

            if (esSolicitudDeCuenta && !cerrado) {
                item {
                    TarjetaRestablecerClave(
                        enabled = !procesando,
                        onRestablecer = { confirmarRestablecer = true }
                    )
                }
            }

            item { Seccion("Conversación") }
            if (t.conversacion.isEmpty()) {
                item { ConversacionVacia("Aún no hay mensajes. Escribe una respuesta para el cliente.") }
            }
            items(t.conversacion, key = { it.id }) { c ->
                BurbujaComentario(
                    autor = c.autorNombre,
                    texto = c.texto,
                    fecha = c.fecha,
                    esPropio = c.autorId == vm.soporteId,
                    interna = c.interno
                )
            }
        }
    }
}

@Composable
private fun SelectorNotaInterna(activa: Boolean, onCambio: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = activa, role = Role.Switch, onValueChange = onCambio)
            .padding(start = 16.dp, end = 12.dp, top = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Lock,
            contentDescription = null,
            tint = if (activa) EstadoAbierto else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            if (activa) "Nota interna: el estudiante no la verá" else "Nota interna",
            style = MaterialTheme.typography.labelMedium,
            color = if (activa) EstadoAbierto else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = activa,
            onCheckedChange = null,
            modifier = Modifier.scale(0.8f)
        )
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

@Composable
private fun TarjetaRestablecerClave(enabled: Boolean, onRestablecer: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Text("Cuenta del estudiante", style = MaterialTheme.typography.titleSmall)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "Si el estudiante no puede ingresar, genera una contraseña temporal. " +
                    "Al usarla, la app le pedirá crear una nueva.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            AyudaButton(
                texto = "Generar contraseña temporal",
                onClick = onRestablecer,
                variante = VarianteBoton.CONTORNO,
                enabled = enabled,
                icono = Icons.Outlined.Key
            )
        }
    }
}

@Composable
private fun DialogoClaveTemporal(clave: String, onCerrar: () -> Unit) {
    val portapapeles = LocalClipboardManager.current
    AlertDialog(
        onDismissRequest = {},
        icon = { Icon(Icons.Outlined.Key, contentDescription = null) },
        title = { Text("Contraseña temporal") },
        text = {
            Column {
                Text(
                    "Entrégala al estudiante por un canal seguro (correo institucional o en persona). " +
                        "No se volverá a mostrar."
                )
                Spacer(Modifier.height(14.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        clave,
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                portapapeles.setText(AnnotatedString(clave))
                onCerrar()
            }) { Text("Copiar y cerrar") }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) { Text("Cerrar") }
        }
    )
}
