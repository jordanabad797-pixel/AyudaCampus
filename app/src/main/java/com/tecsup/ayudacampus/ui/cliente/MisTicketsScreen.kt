package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.data.local.entity.estadoEnum
import com.tecsup.ayudacampus.data.local.entity.prioridadEnum
import com.tecsup.ayudacampus.model.FiltroEstado
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AvisoMesaCerrada
import com.tecsup.ayudacampus.ui.components.EstadoVacio
import com.tecsup.ayudacampus.ui.components.FiltrosEstado
import com.tecsup.ayudacampus.ui.components.TicketCard
import com.tecsup.ayudacampus.viewmodel.MisTicketsViewModel

/**
 * [TÚ LO HACES · Paso 7] Lista de tickets del estudiante con búsqueda y filtros.
 *
 * @param nombreUsuario para el saludo ("Hola, Juan").
 * @param onAbrirTicket abre el detalle (Paso 9).
 * @param onNuevoTicket abre el formulario (Paso 8).
 * @param onCerrarSesion temporal hasta tener la pantalla de Perfil (Paso 12); null = oculto.
 */
@Composable
fun MisTicketsScreen(
    vm: MisTicketsViewModel,
    nombreUsuario: String,
    onAbrirTicket: (Int) -> Unit,
    onNuevoTicket: () -> Unit,
    mesaAbierta: Boolean = true,
    onCerrarSesion: (() -> Unit)? = null
) {
    val tickets by vm.tickets.collectAsStateWithLifecycle()
    val filtro by vm.filtro.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    val abiertos by vm.abiertos.collectAsStateWithLifecycle()

    val primerNombre = nombreUsuario.substringBefore(" ")
    val textoAbiertos = if (abiertos == 1) "1 abierto" else "$abiertos abiertos"

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Mis tickets",
                subtitulo = "Hola, $primerNombre · $textoAbiertos",
                acciones = {
                    if (onCerrarSesion != null) {
                        IconButton(onClick = onCerrarSesion) {
                            Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Cerrar sesión")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (mesaAbierta) FloatingActionButton(
                onClick = onNuevoTicket,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo ticket")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!mesaAbierta) {
                AvisoMesaCerrada(Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
            Buscador(
                texto = busqueda,
                onTexto = vm::buscar,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Spacer(Modifier.height(8.dp))
            FiltrosEstado(seleccionado = filtro, onCambio = vm::cambiarFiltro)
            Spacer(Modifier.height(8.dp))

            when {
                cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                tickets.isEmpty() -> EstadoVacio(
                    icono = Icons.Outlined.ConfirmationNumber,
                    titulo = "No tienes tickets aquí",
                    detalle = when {
                        busqueda.isNotBlank() -> "Ningún ticket coincide con \"${busqueda.trim()}\"."
                        filtro != FiltroEstado.TODOS -> "Prueba con otro filtro o crea un ticket con el botón +."
                        else -> "Cuando reportes una incidencia, aparecerá aquí."
                    }
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tickets, key = { it.ticket.id }) { item ->
                        TicketCard(
                            id = item.ticket.id,
                            titulo = item.ticket.titulo,
                            iconoCategoria = item.categoria.icono,
                            detalle = item.ticket.ubicacion.ifBlank { item.categoria.nombre },
                            estado = item.ticket.estadoEnum,
                            prioridad = item.ticket.prioridadEnum,
                            fecha = item.ticket.fecha,
                            onClick = { onAbrirTicket(item.ticket.id) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

/** Campo de búsqueda redondeado con botón para limpiar. */
@Composable
private fun Buscador(texto: String, onTexto: (String) -> Unit, modifier: Modifier = Modifier) {
    val foco = LocalFocusManager.current
    OutlinedTextField(
        value = texto,
        onValueChange = onTexto,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Buscar por título o número…") },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        trailingIcon = if (texto.isNotEmpty()) {
            {
                IconButton(onClick = { onTexto("") }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Limpiar búsqueda")
                }
            }
        } else null,
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { foco.clearFocus() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}
