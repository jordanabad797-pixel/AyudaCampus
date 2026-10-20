package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.local.entity.estadoEnum
import com.tecsup.ayudacampus.data.local.entity.prioridadEnum
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.CampoBusqueda
import com.tecsup.ayudacampus.ui.components.EstadoVacio
import com.tecsup.ayudacampus.ui.components.FiltrosEstado
import com.tecsup.ayudacampus.ui.components.IconosCategoria
import com.tecsup.ayudacampus.ui.components.TicketCard
import com.tecsup.ayudacampus.ui.components.VarianteBoton
import com.tecsup.ayudacampus.ui.theme.color
import com.tecsup.ayudacampus.viewmodel.BandejaViewModel

@Composable
fun BandejaScreen(
    vm: BandejaViewModel,
    onAtender: (Int) -> Unit,
    onAtras: (() -> Unit)? = null
) {
    val tickets by vm.tickets.collectAsStateWithLifecycle()
    val filtros by vm.filtros.collectAsStateWithLifecycle()
    val categorias by vm.categorias.collectAsStateWithLifecycle()
    val total by vm.total.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    val sla by vm.sla.collectAsStateWithLifecycle()

    var verAvanzados by rememberSaveable { mutableStateOf(false) }
    val avanzadosActivos = filtros.prioridad != null || filtros.categoriaId != null

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Tickets",
                onAtras = onAtras,
                acciones = {
                    IconButton(
                        onClick = { verAvanzados = !verAvanzados },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (verAvanzados || avanzadosActivos) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    ) {
                        Icon(
                            Icons.Outlined.Tune,
                            contentDescription = if (verAvanzados) "Ocultar filtros avanzados" else "Mostrar filtros avanzados"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CampoBusqueda(
                texto = filtros.texto,
                onTexto = vm::buscar,
                placeholder = "Buscar por título, número o cliente…",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Spacer(Modifier.height(8.dp))
            FiltrosEstado(seleccionado = filtros.estado, onCambio = vm::cambiarFiltro)

            AnimatedVisibility(
                visible = verAvanzados || avanzadosActivos,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    EtiquetaFiltro("Prioridad")
                    FiltroPrioridad(seleccionada = filtros.prioridad, onElegir = vm::cambiarPrioridad)
                    EtiquetaFiltro("Categoría")
                    FiltroCategoria(
                        categorias = categorias,
                        seleccionada = filtros.categoriaId,
                        onElegir = vm::cambiarCategoria
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (filtros.activos == 0) "$total tickets" else "${tickets.size} de $total tickets",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                if (filtros.activos > 0) {
                    TextButton(onClick = vm::limpiarFiltros) {
                        Icon(Icons.Outlined.FilterAltOff, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("  Limpiar filtros (${filtros.activos})")
                    }
                }
            }

            when {
                cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                tickets.isEmpty() -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    EstadoVacio(
                        icono = Icons.Outlined.Inbox,
                        titulo = "No hay tickets que coincidan",
                        detalle = if (filtros.activos > 0) "Prueba con otros filtros." else "Aún no hay tickets."
                    )
                    if (filtros.activos > 0) {
                        AyudaButton(
                            texto = "Limpiar filtros",
                            onClick = vm::limpiarFiltros,
                            variante = VarianteBoton.CONTORNO,
                            anchoCompleto = false
                        )
                    }
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tickets, key = { it.ticket.id }) { item ->
                        TicketCard(
                            id = item.ticket.id,
                            titulo = item.ticket.titulo,
                            iconoCategoria = item.categoria.icono,
                            detalle = nombreCorto(item.cliente.nombre),
                            estado = item.ticket.estadoEnum,
                            prioridad = item.ticket.prioridadEnum,
                            fecha = item.ticket.fecha,
                            onClick = { onAtender(item.ticket.id) },
                            modifier = Modifier.animateItem(),
                            sla = sla[item.ticket.id]
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EtiquetaFiltro(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltroPrioridad(seleccionada: Prioridad?, onElegir: (Prioridad) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(Prioridad.ALTA, Prioridad.MEDIA, Prioridad.BAJA).forEach { p ->
            val activa = p == seleccionada
            FilterChip(
                selected = activa,
                onClick = { onElegir(p) },
                label = { Text(p.etiqueta) },
                shape = MaterialTheme.shapes.extraLarge,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = p.color(),
                    selectedContainerColor = p.color(),
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = activa,
                    borderColor = p.color().copy(alpha = 0.4f),
                    selectedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltroCategoria(categorias: List<Categoria>, seleccionada: Int?, onElegir: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categorias.forEach { c ->
            val activa = c.id == seleccionada
            FilterChip(
                selected = activa,
                onClick = { onElegir(c.id) },
                label = { Text(c.nombre) },
                leadingIcon = {
                    Icon(IconosCategoria.icono(c.icono), contentDescription = null, modifier = Modifier.size(18.dp))
                },
                shape = MaterialTheme.shapes.extraLarge,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = activa,
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = Color.Transparent
                )
            )
        }
    }
}

/** "Juan Pérez" → "Juan P." */
private fun nombreCorto(nombre: String): String {
    val partes = nombre.trim().split(" ").filter { it.isNotBlank() }
    return when (partes.size) {
        0 -> ""
        1 -> partes[0]
        else -> "${partes[0]} ${partes[1].first()}."
    }
}
