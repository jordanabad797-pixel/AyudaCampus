package com.tecsup.ayudacampus.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.ui.theme.Exito

/**
 * [ENTREGADO] Línea de progreso de 4 pasos: Abierto → En proceso → Resuelto → Cerrado.
 * Pasos completados: check verde · paso actual: punto del color del rol · pendientes: gris.
 */
@Composable
fun StepperTicket(estado: EstadoTicket, modifier: Modifier = Modifier) {
    val pasos = EstadoTicket.entries
    val actual = estado.ordinal
    val primario = MaterialTheme.colorScheme.primary
    val inactivo = MaterialTheme.colorScheme.outlineVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "Progreso del ticket: ${estado.etiqueta}, paso ${actual + 1} de ${pasos.size}"
            }
    ) {
        pasos.forEachIndexed { i, paso ->
            val completado = i < actual || estado == EstadoTicket.CERRADO
            val esActual = i == actual && !completado
            val colorIzq by animateColorAsState(
                targetValue = if (i == 0) Color.Transparent else if (i <= actual) primario else inactivo,
                label = "lineaIzq$i"
            )
            val colorDer by animateColorAsState(
                targetValue = if (i == pasos.lastIndex) Color.Transparent else if (i < actual) primario else inactivo,
                label = "lineaDer$i"
            )

            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Líneas que unen los círculos (mitad izquierda y mitad derecha de cada paso)
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f).height(3.dp).background(colorIzq))
                        Box(Modifier.weight(1f).height(3.dp).background(colorDer))
                    }
                    when {
                        completado -> Box(
                            modifier = Modifier.size(22.dp).clip(CircleShape).background(Exito),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }

                        esActual -> Box(
                            modifier = Modifier.size(22.dp).clip(CircleShape).background(primario.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(Modifier.size(12.dp).clip(CircleShape).background(primario))
                        }

                        else -> Box(
                            Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(2.dp, inactivo, CircleShape)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = paso.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (esActual) FontWeight.Bold else FontWeight.Medium,
                    color = if (i <= actual) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
