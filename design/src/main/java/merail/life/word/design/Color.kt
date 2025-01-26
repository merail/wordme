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

    val elementPrimary: Color = ColorConstants.white,
    val elementSecondary: Color = ColorConstants.gray,
    val elementBackgroundPrimary: Color = ColorConstants.black,
    val elementBackgroundSecondary: Color = ColorConstants.darkGrey,
    val elementBackgroundInversePrimary: Color = ColorConstants.white,
    val elementBackgroundInverseSecondary: Color = ColorConstants.lightSeaGreen,
    val elementBackgroundPositive: Color = ColorConstants.cyan_500,

    val textPrimary: Color = ColorConstants.white,
    val textNegative: Color = ColorConstants.red,

    val borderPrimary: Color = ColorConstants.white,

    val keyCellBorder: Color = ColorConstants.cyan_500,
    val keyCellBackgroundDefault: Color = ColorConstants.black,
    val keyCellBackgroundAbsent: Color = ColorConstants.darkGrey,
    val keyCellBackgroundPresent: Color = ColorConstants.lightSeaGreen,
    val keyCellBackgroundCorrect: Color = ColorConstants.cyan_500,

    val keyBorder: Color = ColorConstants.liver,
    val keyBackgroundDefault: Color = ColorConstants.black,
    val keyBackgroundAbsent: Color = ColorConstants.darkGrey,
    val keyBackgroundPresent: Color = ColorConstants.lightSeaGreen,
    val keyBackgroundCorrect: Color = ColorConstants.cyan_500,


    val controlKeyBackgroundDisabled: Color = ColorConstants.liver,
    val okControlKeyBackgroundEnabled: Color = ColorConstants.cyan_500,
    val delControlKeyBackgroundEnabled: Color = ColorConstants.white,
    val controlKeyContentDisabled: Color = ColorConstants.white,
    val controlKeyContentEnabled: Color = ColorConstants.black,
)

internal object ColorConstants {
    val white = Color(0xFFFFFFFF)
    val lightSeaGreen = Color(0xFF1ABC9C)
    val gray = Color(0xFF808080)
    val darkGrey = Color(0xFF2E2E2E)
    val liver = Color(0xFF4F4C4D)
    val black = Color(0xFF000000)
    val cyan_500 = Color(0xFF00BCD4)
    val red = Color(0xFFFF0000)
}