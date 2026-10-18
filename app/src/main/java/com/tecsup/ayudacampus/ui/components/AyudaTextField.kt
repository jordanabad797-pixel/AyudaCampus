package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign

/**
 * [ENTREGADO] Campo de texto estándar.
 *
 * @param error si no es null, el campo se pinta en rojo y muestra el mensaje DEBAJO
 *              (los errores de validación van junto al campo, no en un Toast).
 * @param esClave oculta el texto y agrega el botón de mostrar/ocultar.
 * @param maxLength límite opcional de caracteres (muestra el contador).
 */
@Composable
fun AyudaTextField(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    placeholder: String? = null,
    error: String? = null,
    esClave: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLength: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean = true
) {
    var claveVisible by rememberSaveable { mutableStateOf(false) }
    val hayError = error != null

    OutlinedTextField(
        value = valor,
        onValueChange = { nuevo ->
            if (maxLength == null || nuevo.length <= maxLength) onValorChange(nuevo)
        },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = { Text(etiqueta) },
        placeholder = if (placeholder != null) {
            { Text(placeholder) }
        } else null,
        leadingIcon = if (icono != null) {
            { Icon(icono, contentDescription = null) }
        } else null,
        trailingIcon = when {
            esClave -> {
                {
                    IconButton(onClick = { claveVisible = !claveVisible }) {
                        Icon(
                            imageVector = if (claveVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (claveVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            }
            hayError -> {
                { Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
            }
            else -> null
        },
        isError = hayError,
        supportingText = when {
            error != null -> {
                { Text(error) }
            }
            maxLength != null -> {
                { Text("${valor.length}/$maxLength", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) }
            }
            else -> null
        },
        visualTransformation = if (esClave && !claveVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (esClave) KeyboardType.Password else keyboardType,
            imeAction = imeAction,
            capitalization = if (esClave || keyboardType == KeyboardType.Email) {
                KeyboardCapitalization.None
            } else {
                KeyboardCapitalization.Sentences
            }
        ),
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        minLines = if (singleLine) 1 else minLines,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            errorContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}
