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

    val elementPrimary: Color = ColorConstants.white,
    val elementSecondary: Color = ColorConstants.gray,
    val elementBackgroundPrimary: Color = ColorConstants.black,
    val elementBackgroundSecondary: Color = ColorConstants.darkGrey,
    val elementBackgroundInversePrimary: Color = ColorConstants.white,
    val elementBackgroundInverseSecondary: Color = ColorConstants.white_50,
    val elementBackgroundPositive: Color = ColorConstants.cyan_500,

    val textPrimary: Color = ColorConstants.white,

    val borderPrimary: Color = ColorConstants.white,
)

internal object ColorConstants {
    val white = Color(0xFFFFFFFF)
    val white_50 = Color(0xFF1ABC9C)
    val gray = Color(0xFF808080)
    val darkGrey = Color(0xFF2E2E2E)
    val black = Color(0xFF000000)
    val cyan_500 = Color(0xFF00BCD4)
}