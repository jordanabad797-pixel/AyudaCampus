package com.tecsup.ayudacampus.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.ui.theme.color

/** [ENTREGADO] Etiqueta de la prioridad del ticket (Alta, Media, Baja). */
@Composable
fun PrioridadChip(prioridad: Prioridad, modifier: Modifier = Modifier) {
    EtiquetaColor(
        texto = prioridad.etiqueta,
        color = prioridad.color(),
        descripcion = "Prioridad: ${prioridad.etiqueta}",
        modifier = modifier
    )
}
