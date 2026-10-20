package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.data.local.entity.estadoEnum
import com.tecsup.ayudacampus.data.local.entity.prioridadEnum
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.EstadoVacio
import com.tecsup.ayudacampus.ui.components.FiltrosEstado
import com.tecsup.ayudacampus.ui.components.TicketCard
import com.tecsup.ayudacampus.viewmodel.BandejaViewModel

@Composable
fun BandejaScreen(
    vm: BandejaViewModel,
    onAtender: (Int) -> Unit,
    onAtras: (() -> Unit)? = null
) {
    val tickets by vm.tickets.collectAsStateWithLifecycle()
    val filtro by vm.filtro.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    val sla by vm.sla.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(titulo = "Tickets", onAtras = onAtras) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FiltrosEstado(seleccionado = filtro, onCambio = vm::cambiarFiltro)
            Spacer(Modifier.height(8.dp))

            when {
                cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                tickets.isEmpty() -> EstadoVacio(
                    icono = Icons.Outlined.Inbox,
                    titulo = "No hay tickets aquí",
                    detalle = "Prueba con otro filtro."
                )

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

/** "Juan Pérez" → "Juan P." */
private fun nombreCorto(nombre: String): String {
    val partes = nombre.trim().split(" ").filter { it.isNotBlank() }
    return when (partes.size) {
        0 -> ""
        1 -> partes[0]
        else -> "${partes[0]} ${partes[1].first()}."
    }
}
