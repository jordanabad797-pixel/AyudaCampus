package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/** Estilos de botón usados en los mockups. */
enum class VarianteBoton {
    /** Relleno con el color del rol: "Ingresar", "Enviar ticket", "Guardar cambios". */
    PRIMARIO,
    /** Borde del color del rol: "Cancelar". */
    CONTORNO,
    /** Borde rojo: "Reabrir ticket", "Cerrar sesión". */
    PELIGRO
}

/**
 * [ENTREGADO] Botón estándar de AyudaCampus. Toma el color del rol desde el tema.
 * @param cargando muestra un indicador y deshabilita el botón (evita doble envío).
 */
@Composable
fun AyudaButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variante: VarianteBoton = VarianteBoton.PRIMARIO,
    enabled: Boolean = true,
    cargando: Boolean = false,
    icono: ImageVector? = null,
    anchoCompleto: Boolean = true
) {
    val mod = (if (anchoCompleto) modifier.fillMaxWidth() else modifier).heightIn(min = 52.dp)
    val habilitado = enabled && !cargando
    val forma = MaterialTheme.shapes.medium

    val contenido: @Composable RowScope.() -> Unit = {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp,
                color = LocalContentColor.current
            )
        } else {
            if (icono != null) {
                Icon(icono, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(texto, style = MaterialTheme.typography.labelLarge)
        }
    }

    when (variante) {
        VarianteBoton.PRIMARIO -> Button(
            onClick = onClick,
            modifier = mod,
            enabled = habilitado,
            shape = forma,
            content = contenido
        )

        VarianteBoton.CONTORNO -> OutlinedButton(
            onClick = onClick,
            modifier = mod,
            enabled = habilitado,
            shape = forma,
            border = BorderStroke(
                1.5.dp,
                if (habilitado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            ),
            content = contenido
        )

        VarianteBoton.PELIGRO -> OutlinedButton(
            onClick = onClick,
            modifier = mod,
            enabled = habilitado,
            shape = forma,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(
                1.5.dp,
                if (habilitado) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant
            ),
            content = contenido
        )
    }
}
