package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * [ENTREGADO] Un mensaje de la conversación del ticket.
 *
 * @param esPropio true → a la DERECHA con el color del rol ("Tú").
 *                 false → a la IZQUIERDA con el color del otro perfil.
 * @param autor nombre que se muestra cuando el mensaje no es propio (ej. "Soporte TI").
 */
@Composable
fun BurbujaComentario(
    autor: String,
    texto: String,
    fecha: Long,
    esPropio: Boolean,
    modifier: Modifier = Modifier
) {
    val fondo = if (esPropio) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiaryContainer
    val contenido = if (esPropio) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiaryContainer
    // La "colita" de la burbuja apunta hacia quien habla.
    val forma = if (esPropio) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 4.dp, bottomStart = 16.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (esPropio) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = fondo,
            contentColor = contenido,
            shape = forma,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = "${if (esPropio) "Tú" else autor} · ${Formatos.hora(fecha)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalContentColor.current.copy(alpha = 0.75f)
                )
                Spacer(Modifier.height(2.dp))
                Text(text = texto, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
