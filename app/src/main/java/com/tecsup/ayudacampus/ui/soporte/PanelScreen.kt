package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.Formatos
import com.tecsup.ayudacampus.ui.components.NumeroAnimado
import com.tecsup.ayudacampus.ui.components.graficos.GraficoBarras
import com.tecsup.ayudacampus.ui.components.graficos.GraficoDona
import com.tecsup.ayudacampus.ui.theme.Peligro
import com.tecsup.ayudacampus.ui.theme.color
import com.tecsup.ayudacampus.viewmodel.PanelViewModel

/**
 * @param onVerBandeja abre la bandeja (contadores y botón "Ver" de la alerta).
 * @param onVerCategorias / onCerrarSesion: accesos temporales hasta la NavigationBar del Paso 12.
 */
@Composable
fun PanelScreen(
    vm: PanelViewModel,
    onVerBandeja: () -> Unit,
    onVerCategorias: (() -> Unit)? = null,
    onCerrarSesion: (() -> Unit)? = null
) {
    val abiertos by vm.abiertos.collectAsStateWithLifecycle()
    val enProceso by vm.enProceso.collectAsStateWithLifecycle()
    val resueltos by vm.resueltos.collectAsStateWithLifecycle()
    val porEstado by vm.porEstado.collectAsStateWithLifecycle()
    val porCategoria by vm.porCategoria.collectAsStateWithLifecycle()
    val altas by vm.altasSinAtender.collectAsStateWithLifecycle()
    val promedio by vm.promedioResolucion.collectAsStateWithLifecycle()
    val cerrados by vm.cerrados.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Panel de soporte",
                subtitulo = "Mesa de ayuda TI · Hoy",
                acciones = {
                    IconButton(onClick = onVerBandeja) {
                        Icon(Icons.Outlined.Inbox, contentDescription = "Bandeja de tickets")
                    }
                    if (onVerCategorias != null) {
                        IconButton(onClick = onVerCategorias) {
                            Icon(Icons.Outlined.LocalOffer, contentDescription = "Categorías")
                        }
                    }
                    if (onCerrarSesion != null) {
                        IconButton(onClick = onCerrarSesion) {
                            Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Cerrar sesión")
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Contador(abiertos, "Abiertos", EstadoTicket.ABIERTO.color(), onVerBandeja, Modifier.weight(1f))
                Contador(enProceso, "En proceso", EstadoTicket.EN_PROCESO.color(), onVerBandeja, Modifier.weight(1f))
                Contador(resueltos, "Resueltos", EstadoTicket.RESUELTO.color(), onVerBandeja, Modifier.weight(1f))
            }

            AnimatedVisibility(visible = altas > 0) {
                AlertaAltas(cantidad = altas, onVer = onVerBandeja)
            }

            TarjetaPromedio(promedio = promedio, cerrados = cerrados)

            TarjetaGrafico("Tickets por estado") {
                GraficoDona(datos = porEstado)
            }

            TarjetaGrafico("Tickets por categoría") {
                GraficoBarras(datos = porCategoria)
            }
        }
    }
}

@Composable
private fun Contador(
    valor: Int,
    etiqueta: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = color.copy(alpha = 0.14f),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NumeroAnimado(valor = valor, style = MaterialTheme.typography.headlineMedium, color = color)
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = color)
        }
    }
}

@Composable
private fun TarjetaGrafico(titulo: String, contenido: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(16.dp))
            contenido()
        }
    }
}

@Composable
private fun TarjetaPromedio(promedio: Long?, cerrados: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    Icons.Outlined.Timer,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(24.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Tiempo promedio de resolución",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    promedio?.let { Formatos.duracion(it) } ?: "Sin datos",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    when (cerrados) {
                        0 -> "Aún no hay tickets cerrados"
                        1 -> "Basado en 1 ticket cerrado"
                        else -> "Basado en $cerrados tickets cerrados"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AlertaAltas(cantidad: Int, onVer: () -> Unit) {
    Surface(
        color = Peligro.copy(alpha = 0.10f),
        contentColor = Peligro,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, Peligro.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.WarningAmber, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (cantidad == 1) "1 ticket de prioridad Alta" else "$cantidad tickets de prioridad Alta",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    "Sin atender hace más de 1 hora",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onVer,
                colors = ButtonDefaults.buttonColors(containerColor = Peligro, contentColor = Color.White)
            ) {
                Text("Ver")
            }
        }
    }
}
