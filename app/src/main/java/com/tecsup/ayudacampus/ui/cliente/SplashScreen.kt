package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.theme.AyudaTheme

/**
 * [ENTREGADO] Pantalla de bienvenida (mockup 1 del Estudiante).
 * @param onComenzar botón "Comenzar".
 * @param onTengoCuenta enlace "Ya tengo una cuenta".
 */
@Composable
fun SplashScreen(
    onComenzar: () -> Unit,
    onTengoCuenta: () -> Unit
) {
    // Estado de animación de entrada: arranca oculto y pasa a visible al componerse.
    val entrada = remember { MutableTransitionState(false).apply { targetState = true } }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            AnimatedVisibility(
                visibleState = entrada,
                enter = fadeIn(tween(500)) + scaleIn(tween(500), initialScale = 0.8f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Sms,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("AyudaCampus", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Reporta y sigue tus incidencias",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            AnimatedVisibility(
                visibleState = entrada,
                enter = fadeIn(tween(600, delayMillis = 250)) +
                    slideInVertically(tween(600, delayMillis = 250)) { it / 2 }
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    listOf(Icons.Outlined.DesktopWindows, Icons.Outlined.Wifi, Icons.Outlined.Print).forEach { icono ->
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icono,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1.4f))

            AyudaButton(texto = "Comenzar", onClick = onComenzar)
            TextButton(onClick = onTengoCuenta) {
                Text(
                    "Ya tengo una cuenta",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashPreview() {
    AyudaTheme { SplashScreen(onComenzar = {}, onTengoCuenta = {}) }
}
