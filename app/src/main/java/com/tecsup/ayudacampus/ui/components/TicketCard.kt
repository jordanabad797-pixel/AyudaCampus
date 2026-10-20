package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.EstadoSla
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.ui.theme.AyudaTheme
import com.tecsup.ayudacampus.ui.theme.color

/**
 * [ENTREGADO] Tarjeta de un ticket para las listas (Mis tickets y Bandeja).
 *
 * Recibe datos simples (no la entidad) para que sirva en ambos perfiles:
 * @param detalle texto secundario. Estudiante: la ubicación. Soporte: el nombre del cliente.
 */
@Composable
fun TicketCard(
    id: Int,
    titulo: String,
    iconoCategoria: String,
    detalle: String,
    estado: EstadoTicket,
    prioridad: Prioridad,
    fecha: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sla: EstadoSla? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            // Franja izquierda con el color de la prioridad
            Box(
                Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(prioridad.color())
            )
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                IconoCategoria(clave = iconoCategoria, tamano = 40.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${Formatos.numero(id)} · $detalle",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EstadoChip(estado)
                        Spacer(Modifier.width(6.dp))
                        PrioridadChip(prioridad)
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = Formatos.haceCuanto(fecha),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (sla != null) {
                        Spacer(Modifier.height(6.dp))
                        SlaChip(sla)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TicketCardPreview() {
    AyudaTheme {
        TicketCard(
            id = 12,
            titulo = "Sin Wi-Fi en el laboratorio 3",
            iconoCategoria = "wifi",
            detalle = "Lab 3",
            estado = EstadoTicket.EN_PROCESO,
            prioridad = Prioridad.ALTA,
            fecha = System.currentTimeMillis() - 2 * 3_600_000L,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
