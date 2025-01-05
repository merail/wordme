package merail.life.word.design

import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf

internal val LocalWordMeTypography = staticCompositionLocalOf { Typography }

internal val Typography.materialTypography: Typography
    get() = Typography()

val Typography = Typography()