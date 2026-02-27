package merail.life.design

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
    val screenPrimary: Color = ColorConstants.smokeWhite,
    val screenBackground: Color = ColorConstants.black,
    val screenBackgroundSecondary: Color = ColorConstants.darkGrey,

    val elementPrimary: Color = ColorConstants.black,
    val elementSecondary: Color = ColorConstants.liver,
    val elementDisabled: Color = ColorConstants.darkGrey,
    val elementInversePrimary: Color = ColorConstants.smokeWhite,
    val elementPositive: Color = ColorConstants.cyan_500,
    val elementTertiary: Color = ColorConstants.steelGrey,
    val elementNegative: Color = ColorConstants.red,
    val elementNegativeSecondary: Color = ColorConstants.darkRed,
    val elementNegativeTertiary: Color = ColorConstants.darkRed.copy(alpha = 0.6f),

    val textPrimary: Color = ColorConstants.black,
    val textSecondary: Color = ColorConstants.darkGrayishRed,
    val textInversePrimary: Color = ColorConstants.smokeWhite,
    val textNegative: Color = ColorConstants.red,
    val textPositive: Color = ColorConstants.cyan_500,
)

internal object ColorConstants {
    val smokeWhite = Color(0xFFE8EDF5)
    val darkGrey = Color(0xFF252B3A)
    val liver = Color(0xFF424A5E)
    val darkGrayishRed = Color(0xFF7B8799)
    val black = Color(0xFF000000)
    val steelGrey = Color(0xFF343B4C)
    val cyan_500 = Color(0xFF06B6D4)
    val red = Color(0xFFE53E3E)
    val darkRed = Color(0xFF7B1F1F)
}