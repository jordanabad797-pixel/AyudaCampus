package com.tecsup.ayudacampus.ui.components.graficos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * [ENTREGADO] Gráfico de dona dibujado con Canvas (reutilizado del laboratorio de gráficos).
 * Muestra el total en el centro y la leyenda a la derecha.
 */
@Composable
fun GraficoDona(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier,
    subtituloCentro: String = "tickets",
    diametro: Dp = 132.dp,
    grosor: Dp = 22.dp
) {
    val total = datos.sumOf { it.valor.toDouble() }.toFloat()
    val pista = MaterialTheme.colorScheme.surfaceVariant

    // Animación de entrada: los arcos "crecen" de 0 a su tamaño final.
    val progreso = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progreso.animateTo(1f, animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing))
    }

    val resumen = datos.joinToString { "${it.etiqueta}: ${it.valor.toInt()}" }

    Row(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = "Gráfico de dona. Total ${total.toInt()}. $resumen"
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(diametro), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val trazo = grosor.toPx()
                val esquina = Offset(trazo / 2, trazo / 2)
                val tamanoArco = Size(size.width - trazo, size.height - trazo)

                // Pista de fondo
                drawArc(
                    color = pista, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                    topLeft = esquina, size = tamanoArco, style = Stroke(width = trazo)
                )
                if (total > 0f) {
                    var inicio = -90f // empieza arriba (las 12 en punto)
                    datos.forEach { dato ->
                        val barrido = dato.valor / total * 360f * progreso.value
                        drawArc(
                            color = dato.color, startAngle = inicio, sweepAngle = barrido, useCenter = false,
                            topLeft = esquina, size = tamanoArco, style = Stroke(width = trazo)
                        )
                        inicio += barrido
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(total.toInt().toString(), style = MaterialTheme.typography.titleLarge)
                Text(
                    subtituloCentro,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.width(20.dp))

        // Leyenda
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            datos.forEach { dato ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(dato.color)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(dato.etiqueta, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    Text(dato.valor.toInt().toString(), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
