package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.VarianteBoton
import com.tecsup.ayudacampus.viewmodel.RecuperarClaveViewModel

@Composable
fun RecuperarClaveScreen(vm: RecuperarClaveViewModel, onAtras: () -> Unit) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val foco = LocalFocusManager.current
    val enviar: () -> Unit = {
        foco.clearFocus()
        vm.enviar()
    }

    Scaffold(
        topBar = { AppTopBar(titulo = "Recuperar contraseña", onAtras = onAtras) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        AnimatedContent(
            targetState = ui.enviada,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "recuperarClave",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { enviada ->
            if (enviada) {
                SolicitudEnviada(onVolver = onAtras)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Escribe el correo de tu cuenta. Soporte TI recibirá tu solicitud y restablecerá tu acceso.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(24.dp))

                    AyudaTextField(
                        valor = ui.correo,
                        onValorChange = vm::cambiarCorreo,
                        etiqueta = "Correo",
                        icono = Icons.Outlined.MailOutline,
                        placeholder = "nombre@campus.test",
                        error = ui.error,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done,
                        keyboardActions = KeyboardActions(onDone = { enviar() }),
                        enabled = !ui.enviando
                    )
                    Spacer(Modifier.height(8.dp))
                    AyudaButton(texto = "Enviar solicitud", onClick = enviar, cargando = ui.enviando)

                    Spacer(Modifier.height(24.dp))
                    PasosRecuperacion()
                }
            }
        }
    }
}

@Composable
private fun PasosRecuperacion() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("¿Cómo funciona?", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Paso(1, "Se crea un ticket de prioridad Alta para Soporte TI.")
            Paso(2, "Soporte verifica tu identidad y genera una contraseña temporal.")
            Paso(3, "Al ingresar con ella, la app te pedirá crear una contraseña nueva.")
        }
    }
}

@Composable
private fun Paso(numero: Int, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("$numero", style = MaterialTheme.typography.labelMedium)
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(texto, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SolicitudEnviada(onVolver: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("Solicitud registrada", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "Si el correo pertenece a una cuenta de estudiante, Soporte TI atenderá tu solicitud " +
                "y te entregará una contraseña temporal por tu correo institucional.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        AyudaButton(texto = "Volver a iniciar sesión", onClick = onVolver, variante = VarianteBoton.CONTORNO)
    }
}
