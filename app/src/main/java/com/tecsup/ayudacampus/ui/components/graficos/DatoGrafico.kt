package com.tecsup.ayudacampus.ui.components.graficos

import androidx.compose.ui.graphics.Color

/**
 * [ENTREGADO] Un dato para los gráficos (una porción de la dona o una barra).
 * El ViewModel convierte los conteos de Room en una List<DatoGrafico>.
 */
data class DatoGrafico(
    val etiqueta: String,
    val valor: Float,
    val color: Color
)
