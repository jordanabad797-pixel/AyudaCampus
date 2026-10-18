package com.tecsup.ayudacampus.ui.components.graficos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * [ENTREGADO] Gráfico de barras verticales dibujado con Canvas.
 * Cada barra muestra su valor encima y su etiqueta debajo.
 */
@Composable
fun GraficoBarras(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier,
    alto: Dp = 150.dp
) {
    val maximo = (datos.maxOfOrNull { it.valor } ?: 0f).coerceAtLeast(1f)
    val medidorTexto = rememberTextMeasurer()
    val estiloValor = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp
    )
    val colorBase = MaterialTheme.colorScheme.outlineVariant

    val progreso = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progreso.animateTo(1f, animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing))
    }

    val resumen = datos.joinToString { "${it.etiqueta}: ${it.valor.toInt()}" }

    Column(modifier.semantics(mergeDescendants = true) { contentDescription = "Gráfico de barras. $resumen" }) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(alto)
        ) {
            if (datos.isEmpty()) return@Canvas
            val espacio = size.width / datos.size
            val anchoBarra = espacio * 0.5f
            val reservaTexto = 18.dp.toPx() // espacio para el número sobre la barra más alta
            val altoUtil = size.height - reservaTexto

            datos.forEachIndexed { i, dato ->
                val altoBarra = altoUtil * (dato.valor / maximo) * progreso.value
                val x = espacio * i + (espacio - anchoBarra) / 2f
                val y = size.height - altoBarra

                drawRoundRect(
                    color = dato.color,
                    topLeft = Offset(x, y),
                    size = Size(anchoBarra, altoBarra),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                val texto = medidorTexto.measure(dato.valor.toInt().toString(), style = estiloValor)
                drawText(
                    textLayoutResult = texto,
                    topLeft = Offset(
                        x + anchoBarra / 2f - texto.size.width / 2f,
                        y - texto.size.height - 2.dp.toPx()
                    )
                )
            }
            // Línea base
            drawLine(
                color = colorBase,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            datos.forEach { dato ->
                Text(
                    text = dato.etiqueta,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
