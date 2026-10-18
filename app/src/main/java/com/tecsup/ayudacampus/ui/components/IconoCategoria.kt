package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * [ENTREGADO] Traduce la clave guardada en la columna `icono` a un ícono de Material.
 * Claves válidas: pc, code, wifi, key, printer, tag.
 */
object IconosCategoria {
    val claves: List<String> = listOf("pc", "code", "wifi", "key", "printer", "tag")

    fun icono(clave: String): ImageVector = when (clave) {
        "pc" -> Icons.Outlined.DesktopWindows
        "code" -> Icons.Outlined.Code
        "wifi" -> Icons.Outlined.Wifi
        "key" -> Icons.Outlined.VpnKey
        "printer" -> Icons.Outlined.Print
        else -> Icons.Outlined.LocalOffer // "tag" o cualquier valor desconocido
    }
}

/** [ENTREGADO] Ícono de categoría dentro de un cuadro redondeado del color del rol. */
@Composable
fun IconoCategoria(
    clave: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 40.dp,
    activo: Boolean = true
) {
    val fondo = if (activo) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val tinte = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = modifier
            .size(tamano)
            .clip(RoundedCornerShape(tamano * 0.3f))
            .background(fondo),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = IconosCategoria.icono(clave),
            contentDescription = null,
            tint = tinte,
            modifier = Modifier.size(tamano * 0.55f)
        )
    }
}
