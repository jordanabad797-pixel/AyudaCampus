package com.tecsup.ayudacampus.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.tecsup.ayudacampus.model.Rol

/*
 * [ENTREGADO] Tema de AyudaCampus.
 *
 * AyudaTheme(rol, oscuro) entrega un ColorScheme distinto para cada perfil:
 *  - primary / primaryContainer → color del rol (cian o violeta).
 *  - tertiaryContainer → color del OTRO rol: se usa en las burbujas de la conversación
 *    para que los mensajes del otro perfil se distingan a simple vista.
 */

private val ClienteClaro = lightColorScheme(
    primary = Cian, onPrimary = Color.White,
    primaryContainer = CianContenedor, onPrimaryContainer = CianSobreContenedor,
    secondary = Cian, onSecondary = Color.White,
    secondaryContainer = CianContenedor, onSecondaryContainer = CianSobreContenedor,
    tertiary = Violeta, onTertiary = Color.White,
    tertiaryContainer = VioletaContenedor, onTertiaryContainer = VioletaSobreContenedor,
    background = CianFondo, onBackground = TextoPrincipal,
    surface = Color.White, onSurface = TextoPrincipal,
    surfaceVariant = SuperficieVariante, onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color.White,
    surfaceContainer = Color.White, surfaceContainerHigh = Color(0xFFF8FAFC),
    surfaceContainerHighest = SuperficieVariante,
    outline = Borde, outlineVariant = BordeSuave,
    error = Peligro, onError = Color.White
)

private val ClienteOscuro = darkColorScheme(
    primary = CianOscuro, onPrimary = CianSobreOscuro,
    primaryContainer = CianContenedorOscuro, onPrimaryContainer = CianContenedor,
    secondary = CianOscuro, onSecondary = CianSobreOscuro,
    secondaryContainer = CianContenedorOscuro, onSecondaryContainer = CianContenedor,
    tertiary = VioletaOscuro, onTertiary = VioletaSobreOscuro,
    tertiaryContainer = VioletaContenedorOscuro, onTertiaryContainer = VioletaContenedor,
    background = CianFondoOscuro, onBackground = TextoPrincipalOscuro,
    surface = CianSuperficieOscura, onSurface = TextoPrincipalOscuro,
    surfaceVariant = CianVarianteOscura, onSurfaceVariant = TextoSecundarioOscuro,
    surfaceContainerLowest = CianFondoOscuro, surfaceContainerLow = CianSuperficieOscura,
    surfaceContainer = CianSuperficieOscura, surfaceContainerHigh = CianVarianteOscura,
    surfaceContainerHighest = CianVarianteOscura,
    outline = BordeOscuro, outlineVariant = BordeSuaveOscuro,
    error = Color(0xFFF87171), onError = Color(0xFF450A0A)
)

private val SoporteClaro = lightColorScheme(
    primary = Violeta, onPrimary = Color.White,
    primaryContainer = VioletaContenedor, onPrimaryContainer = VioletaSobreContenedor,
    secondary = Violeta, onSecondary = Color.White,
    secondaryContainer = VioletaContenedor, onSecondaryContainer = VioletaSobreContenedor,
    tertiary = Cian, onTertiary = Color.White,
    tertiaryContainer = CianContenedor, onTertiaryContainer = CianSobreContenedor,
    background = VioletaFondo, onBackground = TextoPrincipal,
    surface = Color.White, onSurface = TextoPrincipal,
    surfaceVariant = SuperficieVariante, onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color.White,
    surfaceContainer = Color.White, surfaceContainerHigh = Color(0xFFF8FAFC),
    surfaceContainerHighest = SuperficieVariante,
    outline = Borde, outlineVariant = BordeSuave,
    error = Peligro, onError = Color.White
)

private val SoporteOscuro = darkColorScheme(
    primary = VioletaOscuro, onPrimary = VioletaSobreOscuro,
    primaryContainer = VioletaContenedorOscuro, onPrimaryContainer = VioletaContenedor,
    secondary = VioletaOscuro, onSecondary = VioletaSobreOscuro,
    secondaryContainer = VioletaContenedorOscuro, onSecondaryContainer = VioletaContenedor,
    tertiary = CianOscuro, onTertiary = CianSobreOscuro,
    tertiaryContainer = CianContenedorOscuro, onTertiaryContainer = CianContenedor,
    background = VioletaFondoOscuro, onBackground = TextoPrincipalOscuro,
    surface = VioletaSuperficieOscura, onSurface = TextoPrincipalOscuro,
    surfaceVariant = VioletaVarianteOscura, onSurfaceVariant = TextoSecundarioOscuro,
    surfaceContainerLowest = VioletaFondoOscuro, surfaceContainerLow = VioletaSuperficieOscura,
    surfaceContainer = VioletaSuperficieOscura, surfaceContainerHigh = VioletaVarianteOscura,
    surfaceContainerHighest = VioletaVarianteOscura,
    outline = BordeOscuro, outlineVariant = BordeSuaveOscuro,
    error = Color(0xFFF87171), onError = Color(0xFF450A0A)
)

/** Formas redondeadas consistentes en toda la app. */
val AyudaShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/** Devuelve el esquema de colores del perfil indicado. */
fun esquemaPara(rol: Rol, oscuro: Boolean): ColorScheme = when (rol) {
    Rol.CLIENTE -> if (oscuro) ClienteOscuro else ClienteClaro
    Rol.ADMIN -> if (oscuro) SoporteOscuro else SoporteClaro
}

/**
 * Tema de la app. Toda pantalla se dibuja dentro de AyudaTheme.
 * @param rol perfil activo: decide el color primario (cian o violeta).
 * @param oscuro modo oscuro; más adelante vendrá de DataStore.
 */
@Composable
fun AyudaTheme(
    rol: Rol = Rol.CLIENTE,
    oscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val esquema = esquemaPara(rol, oscuro)

    // Íconos de la barra de estado: oscuros sobre fondo claro y viceversa.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !oscuro
                isAppearanceLightNavigationBars = !oscuro
            }
        }
    }

    MaterialTheme(
        colorScheme = esquema,
        typography = AyudaTypography,
        shapes = AyudaShapes,
        content = content
    )
}
