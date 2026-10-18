package com.tecsup.ayudacampus.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.ui.theme.Estrella

/**
 * [ENTREGADO] Calificación de 1 a 5 estrellas.
 *
 * @param calificacion valor actual (0 = sin calificar).
 * @param onCambio si es null, las estrellas son de solo lectura (ticket CERRADO).
 */
@Composable
fun Estrellas(
    calificacion: Int,
    modifier: Modifier = Modifier,
    onCambio: ((Int) -> Unit)? = null,
    tamano: Dp = 40.dp
) {
    val soloLectura = onCambio == null
    Row(
        modifier = if (soloLectura) {
            modifier.semantics(mergeDescendants = true) { contentDescription = "$calificacion de 5 estrellas" }
        } else modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (i in 1..5) {
            val llena = i <= calificacion
            val escala by animateFloatAsState(if (llena) 1f else 0.9f, label = "estrella$i")
            val base = Modifier
                .size(tamano)
                .scale(escala)
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = if (soloLectura) null else "Calificar con $i de 5",
                tint = if (llena) Estrella else MaterialTheme.colorScheme.outlineVariant,
                modifier = if (onCambio != null) {
                    base.clip(CircleShape).clickable(role = Role.Button) { onCambio(i) }
                } else base
            )
        }
    }
}
