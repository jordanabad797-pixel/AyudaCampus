package com.tecsup.ayudacampus.ui.cliente

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.model.Validaciones
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.viewmodel.CambiarClaveViewModel

/** Sirve para el cambio voluntario (Perfil / Ajustes) y para el obligatorio tras una contraseña temporal. */
@Composable
fun CambiarClaveScreen(vm: CambiarClaveViewModel, onAtras: () -> Unit) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val foco = LocalFocusManager.current
    val siguiente = KeyboardActions(onNext = { foco.moveFocus(FocusDirection.Down) })
    val guardar: () -> Unit = {
        foco.clearFocus()
        vm.guardar()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = if (vm.obligatorio) "Crea una nueva contraseña" else "Cambiar contraseña",
                onAtras = onAtras
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            if (vm.obligatorio) {
                AvisoClaveTemporal()
                Spacer(Modifier.height(20.dp))
            }

            AyudaTextField(
                valor = ui.actual,
                onValorChange = vm::cambiarActual,
                etiqueta = if (vm.obligatorio) "Contraseña temporal" else "Contraseña actual",
                icono = Icons.Outlined.Key,
                error = ui.errorActual,
                esClave = true,
                keyboardActions = siguiente,
                enabled = !ui.guardando
            )
            AyudaTextField(
                valor = ui.nueva,
                onValorChange = vm::cambiarNueva,
                etiqueta = "Nueva contraseña (mínimo ${Validaciones.MIN_CLAVE} caracteres)",
                icono = Icons.Outlined.Lock,
                error = ui.errorNueva,
                esClave = true,
                keyboardActions = siguiente,
                enabled = !ui.guardando
            )
            AyudaTextField(
                valor = ui.confirmacion,
                onValorChange = vm::cambiarConfirmacion,
                etiqueta = "Confirmar nueva contraseña",
                icono = Icons.Outlined.Lock,
                error = ui.errorConfirmacion,
                esClave = true,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { guardar() }),
                enabled = !ui.guardando
            )

            ui.errorGeneral?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
            AyudaButton(
                texto = if (vm.obligatorio) "Guardar y entrar" else "Guardar contraseña",
                onClick = guardar,
                cargando = ui.guardando
            )
        }
    }
}

@Composable
private fun AvisoClaveTemporal() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Estás usando una contraseña temporal", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Por seguridad, crea una contraseña propia antes de continuar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
