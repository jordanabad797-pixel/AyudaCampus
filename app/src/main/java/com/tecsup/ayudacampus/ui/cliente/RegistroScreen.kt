package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.model.Validaciones
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.IndicadorFuerzaClave
import com.tecsup.ayudacampus.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(vm: RegistroViewModel, onAtras: () -> Unit) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val foco = LocalFocusManager.current
    val siguiente = KeyboardActions(onNext = { foco.moveFocus(FocusDirection.Down) })
    val registrar: () -> Unit = {
        foco.clearFocus()
        vm.registrar()
    }

    Scaffold(
        topBar = { AppTopBar(titulo = "Crear cuenta", onAtras = onAtras) },
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
            Text("Regístrate como estudiante", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Text(
                "Podrás reportar incidencias y seguir su atención.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            AyudaTextField(
                valor = ui.nombre,
                onValorChange = vm::cambiarNombre,
                etiqueta = "Nombre completo",
                icono = Icons.Outlined.Person,
                error = ui.errorNombre,
                maxLength = Validaciones.MAX_NOMBRE,
                keyboardActions = siguiente,
                enabled = !ui.enviando
            )
            AyudaTextField(
                valor = ui.correo,
                onValorChange = vm::cambiarCorreo,
                etiqueta = "Correo institucional",
                icono = Icons.Outlined.MailOutline,
                placeholder = "nombre@${Validaciones.DOMINIO_INSTITUCIONAL}",
                error = ui.errorCorreo,
                keyboardType = KeyboardType.Email,
                keyboardActions = siguiente,
                enabled = !ui.enviando
            )
            AyudaTextField(
                valor = ui.clave,
                onValorChange = vm::cambiarClave,
                etiqueta = "Contraseña (mín. ${Validaciones.MIN_CLAVE}, letras y números)",
                icono = Icons.Outlined.Lock,
                error = ui.errorClave,
                esClave = true,
                keyboardActions = siguiente,
                enabled = !ui.enviando
            )
            IndicadorFuerzaClave(clave = ui.clave)
            AyudaTextField(
                valor = ui.confirmacion,
                onValorChange = vm::cambiarConfirmacion,
                etiqueta = "Confirmar contraseña",
                icono = Icons.Outlined.Lock,
                error = ui.errorConfirmacion,
                esClave = true,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { registrar() }),
                enabled = !ui.enviando
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
            AyudaButton(texto = "Crear cuenta", onClick = registrar, cargando = ui.enviando)

            TextButton(
                onClick = onAtras,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión", textAlign = TextAlign.Center)
            }
        }
    }
}
