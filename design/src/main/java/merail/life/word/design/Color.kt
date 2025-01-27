package merail.life.word.design

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalWordMeColors = staticCompositionLocalOf {
    Colors()
}

val Colors.materialThemeColors: ColorScheme
    get() = lightColorScheme(
        primary = screenPrimary,
        background = screenBackground,
        onBackground = screenPrimary,
        surface = screenBackground,
    )

@Immutable
data class Colors(
    val screenPrimary: Color = ColorConstants.white,
    val screenBackground: Color = ColorConstants.black,
    val screenBackgroundSecondary: Color = ColorConstants.darkGrey,

    val elementPrimary: Color = ColorConstants.black,
    val elementSecondary: Color = ColorConstants.liver,
    val elementDisabled: Color = ColorConstants.darkGrey,
    val elementInversePrimary: Color = ColorConstants.white,
    val elementPositiveSecondary: Color = ColorConstants.lightSeaGreen,
    val elementPositive: Color = ColorConstants.cyan_500,

    val textPrimary: Color = ColorConstants.black,
    val textInversePrimary: Color = ColorConstants.white,
    val textNegative: Color = ColorConstants.red,
)

internal object ColorConstants {
    val white = Color(0xFFFFFFFF)
    val lightSeaGreen = Color(0xFF1ABC9C)
    val darkGrey = Color(0xFF2E2E2E)
    val liver = Color(0xFF4F4C4D)
    val black = Color(0xFF000000)
    val cyan_500 = Color(0xFF00BCD4)
    val red = Color(0xFFFF0000)
}