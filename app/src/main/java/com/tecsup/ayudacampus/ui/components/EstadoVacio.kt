package com.tecsup.ayudacampus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Mensaje amigable cuando una lista no tiene elementos.
 * Con [ilustracion] dibuja una ilustración propia (Canvas); si no, muestra el [icono].
 * [accion] permite agregar un botón (por ejemplo, "Limpiar filtros").
 */
@Composable
fun EstadoVacio(
    icono: ImageVector,
    titulo: String,
    modifier: Modifier = Modifier,
    detalle: String? = null,
    ilustracion: Ilustracion? = null,
    accion: (@Composable () -> Unit)? = null
) {
    val entrada = remember { MutableTransitionState(false).apply { targetState = true } }

    AnimatedVisibility(
        visibleState = entrada,
        enter = fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.92f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (ilustracion != null) {
                IlustracionVacia(ilustracion)
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(titulo, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            if (detalle != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    detalle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            if (accion != null) {
                Spacer(Modifier.height(16.dp))
                accion()
            }
        }
    }
}
