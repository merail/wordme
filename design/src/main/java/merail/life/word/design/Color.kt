package merail.life.word.design

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalWordMeColors = staticCompositionLocalOf {
    Colors
}

val Colors.materialThemeColors: ColorScheme
    get() = lightColorScheme()

@Immutable
data object Colors

internal object ColorConstants