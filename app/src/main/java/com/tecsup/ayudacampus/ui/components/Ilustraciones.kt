package com.tecsup.ayudacampus.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class Ilustracion { TICKETS, BANDEJA, CATEGORIAS, CONVERSACION, BUSQUEDA }

/** Colores tomados del tema: así las ilustraciones funcionan en claro y oscuro, y en cian o violeta. */
private data class Paleta(
    val fondo: Color,
    val superficie: Color,
    val borde: Color,
    val linea: Color,
    val primario: Color,
    val sobrePrimario: Color,
    val secundario: Color,
    val sobreSecundario: Color
)

/** Ilustración vectorial propia (Canvas, sin imágenes) que flota suavemente. */
@Composable
fun IlustracionVacia(ilustracion: Ilustracion, modifier: Modifier = Modifier, tamano: Dp = 150.dp) {
    val esquema = MaterialTheme.colorScheme
    val paleta = Paleta(
        fondo = esquema.primaryContainer,
        superficie = esquema.surface,
        borde = esquema.outline,
        linea = esquema.outlineVariant,
        primario = esquema.primary,
        sobrePrimario = esquema.onPrimary,
        secundario = esquema.tertiaryContainer,
        sobreSecundario = esquema.onTertiaryContainer
    )

    val flotar = rememberInfiniteTransition(label = "flotar")
    val desplazamiento by flotar.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "desplazamiento"
    )

    Canvas(
        modifier
            .size(tamano)
            .graphicsLayer { translationY = desplazamiento * 4.dp.toPx() }
    ) {
        drawCircle(paleta.fondo, radius = size.minDimension * 0.46f, center = center)
        when (ilustracion) {
            Ilustracion.TICKETS -> ticket(paleta)
            Ilustracion.BANDEJA -> bandeja(paleta)
            Ilustracion.CATEGORIAS -> etiqueta(paleta)
            Ilustracion.CONVERSACION -> conversacion(paleta)
            Ilustracion.BUSQUEDA -> lupa(paleta)
        }
    }
}

private fun DrawScope.lineaTexto(color: Color, x: Float, y: Float, ancho: Float, alto: Float) {
    drawRoundRect(color, Offset(x, y), Size(ancho, alto), CornerRadius(alto / 2))
}

private fun DrawScope.ticket(p: Paleta) {
    val w = size.width
    val h = size.height
    val izq = w * 0.16f
    val arriba = h * 0.30f
    val ancho = w * 0.68f
    val alto = h * 0.40f
    val radio = CornerRadius(w * 0.05f)

    drawRoundRect(p.superficie, Offset(izq, arriba), Size(ancho, alto), radio)
    drawRoundRect(p.borde, Offset(izq, arriba), Size(ancho, alto), radio, style = Stroke(w * 0.012f))

    val centroY = arriba + alto / 2
    drawCircle(p.fondo, w * 0.045f, Offset(izq, centroY))
    drawCircle(p.fondo, w * 0.045f, Offset(izq + ancho, centroY))

    val corte = izq + ancho * 0.70f
    drawLine(
        p.borde, Offset(corte, arriba + alto * 0.14f), Offset(corte, arriba + alto * 0.86f),
        strokeWidth = w * 0.01f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(w * 0.025f, w * 0.02f))
    )

    lineaTexto(p.primario, izq + ancho * 0.10f, arriba + alto * 0.24f, ancho * 0.45f, alto * 0.12f)
    lineaTexto(p.linea, izq + ancho * 0.10f, arriba + alto * 0.48f, ancho * 0.50f, alto * 0.09f)
    lineaTexto(p.linea, izq + ancho * 0.10f, arriba + alto * 0.66f, ancho * 0.34f, alto * 0.09f)

    val insignia = Offset(izq + ancho * 0.85f, centroY)
    val r = w * 0.06f
    drawCircle(p.primario, r, insignia)
    val check = Path().apply {
        moveTo(insignia.x - r * 0.45f, insignia.y)
        lineTo(insignia.x - r * 0.10f, insignia.y + r * 0.35f)
        lineTo(insignia.x + r * 0.45f, insignia.y - r * 0.35f)
    }
    drawPath(check, p.sobrePrimario, style = Stroke(w * 0.015f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.bandeja(p: Paleta) {
    val w = size.width
    val h = size.height

    val hojaIzq = w * 0.32f
    val hojaArriba = h * 0.20f
    val hojaAncho = w * 0.36f
    val hojaAlto = h * 0.40f
    drawRoundRect(p.superficie, Offset(hojaIzq, hojaArriba), Size(hojaAncho, hojaAlto), CornerRadius(w * 0.03f))
    drawRoundRect(
        p.borde, Offset(hojaIzq, hojaArriba), Size(hojaAncho, hojaAlto), CornerRadius(w * 0.03f),
        style = Stroke(w * 0.01f)
    )
    lineaTexto(p.primario, hojaIzq + hojaAncho * 0.15f, hojaArriba + hojaAlto * 0.15f, hojaAncho * 0.55f, h * 0.035f)
    lineaTexto(p.linea, hojaIzq + hojaAncho * 0.15f, hojaArriba + hojaAlto * 0.32f, hojaAncho * 0.70f, h * 0.03f)
    lineaTexto(p.linea, hojaIzq + hojaAncho * 0.15f, hojaArriba + hojaAlto * 0.46f, hojaAncho * 0.50f, h * 0.03f)

    val bandeja = Path().apply {
        moveTo(w * 0.18f, h * 0.52f)
        lineTo(w * 0.32f, h * 0.52f)
        lineTo(w * 0.37f, h * 0.61f)
        lineTo(w * 0.63f, h * 0.61f)
        lineTo(w * 0.68f, h * 0.52f)
        lineTo(w * 0.82f, h * 0.52f)
        lineTo(w * 0.82f, h * 0.76f)
        lineTo(w * 0.18f, h * 0.76f)
        close()
    }
    drawPath(bandeja, p.primario)
    drawPath(bandeja, p.primario, style = Stroke(w * 0.02f, join = StrokeJoin.Round))
}

private fun DrawScope.etiqueta(p: Paleta) {
    val w = size.width
    val h = size.height

    fun forma(dx: Float, dy: Float) = Path().apply {
        moveTo(w * 0.20f + dx, h * 0.32f + dy)
        lineTo(w * 0.54f + dx, h * 0.32f + dy)
        lineTo(w * 0.78f + dx, h * 0.50f + dy)
        lineTo(w * 0.54f + dx, h * 0.68f + dy)
        lineTo(w * 0.20f + dx, h * 0.68f + dy)
        close()
    }

    drawPath(forma(w * 0.05f, -h * 0.08f), p.secundario)
    val principal = forma(0f, 0f)
    drawPath(principal, p.primario)
    drawPath(principal, p.primario, style = Stroke(w * 0.02f, join = StrokeJoin.Round))
    drawCircle(p.fondo, w * 0.035f, Offset(w * 0.60f, h * 0.50f))
    lineaTexto(p.sobrePrimario.copy(alpha = 0.85f), w * 0.27f, h * 0.43f, w * 0.20f, h * 0.035f)
    lineaTexto(p.sobrePrimario.copy(alpha = 0.6f), w * 0.27f, h * 0.53f, w * 0.13f, h * 0.035f)
}

private fun DrawScope.conversacion(p: Paleta) {
    val w = size.width
    val h = size.height

    val izq = Offset(w * 0.14f, h * 0.24f)
    val tamIzq = Size(w * 0.50f, h * 0.24f)
    drawRoundRect(p.secundario, izq, tamIzq, CornerRadius(w * 0.07f))
    drawPath(
        Path().apply {
            moveTo(izq.x + w * 0.06f, izq.y + tamIzq.height - 1f)
            lineTo(izq.x + w * 0.04f, izq.y + tamIzq.height + h * 0.07f)
            lineTo(izq.x + w * 0.16f, izq.y + tamIzq.height - 1f)
            close()
        },
        p.secundario
    )
    listOf(0.30f, 0.39f, 0.48f).forEach { x ->
        drawCircle(p.sobreSecundario.copy(alpha = 0.7f), w * 0.025f, Offset(w * x, izq.y + tamIzq.height / 2))
    }

    val der = Offset(w * 0.36f, h * 0.54f)
    val tamDer = Size(w * 0.50f, h * 0.22f)
    drawRoundRect(p.primario, der, tamDer, CornerRadius(w * 0.07f))
    drawPath(
        Path().apply {
            moveTo(der.x + tamDer.width - w * 0.16f, der.y + tamDer.height - 1f)
            lineTo(der.x + tamDer.width - w * 0.04f, der.y + tamDer.height + h * 0.07f)
            lineTo(der.x + tamDer.width - w * 0.06f, der.y + tamDer.height - 1f)
            close()
        },
        p.primario
    )
    lineaTexto(p.sobrePrimario.copy(alpha = 0.9f), der.x + w * 0.06f, der.y + tamDer.height * 0.30f, w * 0.30f, h * 0.035f)
    lineaTexto(p.sobrePrimario.copy(alpha = 0.6f), der.x + w * 0.06f, der.y + tamDer.height * 0.58f, w * 0.20f, h * 0.035f)
}

private fun DrawScope.lupa(p: Paleta) {
    val w = size.width
    val h = size.height
    val centroLente = Offset(w * 0.45f, h * 0.45f)
    val radio = w * 0.19f

    drawCircle(p.superficie, radio, centroLente)
    lineaTexto(p.linea, centroLente.x - radio * 0.55f, centroLente.y - radio * 0.25f, radio * 1.1f, h * 0.035f)
    lineaTexto(p.linea, centroLente.x - radio * 0.55f, centroLente.y + radio * 0.12f, radio * 0.7f, h * 0.035f)
    drawCircle(p.primario, radio, centroLente, style = Stroke(w * 0.045f))
    drawLine(
        p.primario,
        start = Offset(centroLente.x + radio * 0.72f, centroLente.y + radio * 0.72f),
        end = Offset(w * 0.76f, h * 0.76f),
        strokeWidth = w * 0.07f,
        cap = StrokeCap.Round
    )
    drawCircle(p.primario.copy(alpha = 0.5f), w * 0.022f, Offset(w * 0.76f, h * 0.24f))
    drawCircle(p.primario.copy(alpha = 0.35f), w * 0.015f, Offset(w * 0.22f, h * 0.74f))
}
