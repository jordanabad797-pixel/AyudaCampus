package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.ui.theme.color

/** [ENTREGADO] Etiqueta del estado del ticket (Abierto, En proceso, Resuelto, Cerrado). */
@Composable
fun EstadoChip(estado: EstadoTicket, modifier: Modifier = Modifier) {
    EtiquetaColor(
        texto = estado.etiqueta,
        color = estado.color(),
        descripcion = "Estado: ${estado.etiqueta}",
        modifier = modifier
    )
}

/**
 * Píldora de color con fondo translúcido. Base de EstadoChip y PrioridadChip.
 * Funciona igual en modo claro y oscuro porque el fondo es el mismo color con transparencia.
 */
@Composable
internal fun EtiquetaColor(
    texto: String,
    color: Color,
    descripcion: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.semantics { contentDescription = descripcion },
        color = color.copy(alpha = 0.14f),
        contentColor = color,
        shape = RoundedCornerShape(percent = 50)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}
