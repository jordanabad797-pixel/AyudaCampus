package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TimerOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.EstadoSla
import com.tecsup.ayudacampus.model.NivelSla
import com.tecsup.ayudacampus.ui.theme.EstadoAbierto
import com.tecsup.ayudacampus.ui.theme.Exito
import com.tecsup.ayudacampus.ui.theme.Peligro

/** Verde: en tiempo · Ámbar: queda el 25 % o menos · Rojo: "Vencido". */
@Composable
fun SlaChip(sla: EstadoSla, modifier: Modifier = Modifier) {
    val color = when (sla.nivel) {
        NivelSla.EN_TIEMPO -> Exito
        NivelSla.POR_VENCER -> EstadoAbierto
        NivelSla.VENCIDO -> Peligro
    }
    val texto = if (sla.vencido) "Vencido" else "Quedan ${Formatos.duracion(sla.restanteMs)}"
    val descripcion = if (sla.vencido) {
        "SLA vencido. Plazo de ${sla.plazoHoras} horas"
    } else {
        "SLA: quedan ${Formatos.duracion(sla.restanteMs)} de ${sla.plazoHoras} horas"
    }

    Surface(
        modifier = modifier.semantics(mergeDescendants = true) { contentDescription = descripcion },
        color = color.copy(alpha = 0.12f),
        contentColor = color,
        shape = RoundedCornerShape(percent = 50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (sla.vencido) Icons.Outlined.TimerOff else Icons.Outlined.Timer,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = texto, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
    }
}
