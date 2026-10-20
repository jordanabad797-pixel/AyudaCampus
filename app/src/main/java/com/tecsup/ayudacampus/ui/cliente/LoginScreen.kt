package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.viewmodel.SesionViewModel
import kotlinx.coroutines.launch

/**
 * [TÚ LO HACES · Paso 6] Inicio de sesión: UN solo Composable para ambos roles.
 *
 * La pantalla solo guarda lo que el usuario escribe y avisa al ViewModel.
 * Las validaciones y el login viven en [SesionViewModel].
 * Al elegir la pestaña, todo el tema cambia de color (cian ↔ violeta).
 */
@Composable
fun LoginScreen(vm: SesionViewModel, onRegistrarse: () -> Unit = {}) {
    val rol by vm.rolSeleccionado.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()

    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }

    val foco = LocalFocusManager.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val ingresar: () -> Unit = {
        foco.clearFocus()
        vm.login(correo, clave, rol)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text("Bienvenido", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "Ingresa para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(28.dp))
            SelectorRol(seleccionado = rol, enabled = !cargando, onCambio = vm::seleccionarRol)
            Spacer(Modifier.height(24.dp))

            AyudaTextField(
                valor = correo,
                onValorChange = { correo = it; vm.limpiarError() },
                etiqueta = "Correo",
                icono = Icons.Outlined.MailOutline,
                placeholder = "nombre@campus.test",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { foco.moveFocus(FocusDirection.Down) }),
                enabled = !cargando
            )
            Spacer(Modifier.height(4.dp))
            AyudaTextField(
                valor = clave,
                onValorChange = { clave = it; vm.limpiarError() },
                etiqueta = "Contraseña",
                icono = Icons.Outlined.Lock,
                esClave = true,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { ingresar() }),
                enabled = !cargando
            )

            TextButton(
                onClick = {
                    scope.launch { snackbar.showSnackbar("Pide a Soporte TI que restablezca tu contraseña.") }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.labelMedium)
            }

            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                MensajeError(texto = error.orEmpty(), modifier = Modifier.padding(bottom = 12.dp))
            }

            AyudaButton(texto = "Ingresar", onClick = ingresar, cargando = cargando)

            AnimatedVisibility(visible = rol == Rol.CLIENTE) {
                TextButton(
                    onClick = onRegistrarse,
                    enabled = !cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("¿No tienes cuenta? ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Regístrate",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            TarjetaModoPractica(rol)
        }
    }
}

/** Selector Estudiante / Soporte TI (dos pestañas tipo "píldora"). */
@Composable
private fun SelectorRol(seleccionado: Rol, enabled: Boolean, onCambio: (Rol) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
            .selectableGroup()
    ) {
        Rol.entries.forEach { r ->
            val activo = r == seleccionado
            val fondo by animateColorAsState(
                if (activo) MaterialTheme.colorScheme.primary else Color.Transparent,
                label = "fondoRol"
            )
            val texto by animateColorAsState(
                if (activo) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "textoRol"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.small)
                    .background(fondo)
                    .selectable(
                        selected = activo,
                        enabled = enabled,
                        role = Role.Tab,
                        onClick = { onCambio(r) }
                    )
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(r.etiqueta, style = MaterialTheme.typography.labelLarge, color = texto)
            }
        }
    }
}

@Composable
private fun MensajeError(texto: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.10f),
        contentColor = MaterialTheme.colorScheme.error,
        shape = MaterialTheme.shapes.small
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(texto, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** Ayuda para probar la app: credenciales de ejemplo según la pestaña. */
@Composable
private fun TarjetaModoPractica(rol: Rol) {
    val (correo, clave) = when (rol) {
        Rol.CLIENTE -> "juan@campus.test" to "1234"
        Rol.ADMIN -> "soporte@campus.test" to "admin"
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(
                Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    "Modo práctica",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Cuenta de prueba: $correo · $clave",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Puedes cambiar de perfil desde Ajustes para probar ambos roles.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
