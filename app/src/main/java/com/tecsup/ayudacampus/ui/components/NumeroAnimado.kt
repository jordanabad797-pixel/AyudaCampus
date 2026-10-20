package com.tecsup.ayudacampus.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/** Número que "rueda" hacia arriba cuando sube y hacia abajo cuando baja. */
@Composable
fun NumeroAnimado(valor: Int, style: TextStyle, color: Color, modifier: Modifier = Modifier) {
    AnimatedContent(
        targetState = valor,
        modifier = modifier,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically { it } + fadeIn()) togetherWith (slideOutVertically { -it } + fadeOut())
            } else {
                (slideInVertically { -it } + fadeIn()) togetherWith (slideOutVertically { it } + fadeOut())
            }
        },
        label = "numeroAnimado"
    ) { numero ->
        Text(text = numero.toString(), style = style, color = color)
    }
}
