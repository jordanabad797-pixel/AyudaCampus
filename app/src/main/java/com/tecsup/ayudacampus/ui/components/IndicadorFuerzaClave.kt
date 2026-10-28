package com.tecsup.ayudacampus.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.FuerzaClave
import com.tecsup.ayudacampus.model.Validaciones
import com.tecsup.ayudacampus.ui.theme.EstadoAbierto
import com.tecsup.ayudacampus.ui.theme.Exito
import com.tecsup.ayudacampus.ui.theme.Peligro

/** Barra de 3 tramos (débil / media / fuerte) que se actualiza mientras se escribe la contraseña. */
@Composable
fun IndicadorFuerzaClave(clave: String, modifier: Modifier = Modifier) {
    if (clave.isEmpty()) return
    val fuerza = Validaciones.fuerzaClave(clave)
    val nivel = fuerza.ordinal + 1
    val color by animateColorAsState(
        when (fuerza) {
            FuerzaClave.DEBIL -> Peligro
            FuerzaClave.MEDIA -> EstadoAbierto
            FuerzaClave.FUERTE -> Exito
        },
        label = "fuerzaClave"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
            .semantics { contentDescription = "Seguridad de la contraseña: ${fuerza.etiqueta}" },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(FuerzaClave.entries.size) { i ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (i < nivel) color else MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(fuerza.etiqueta, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

