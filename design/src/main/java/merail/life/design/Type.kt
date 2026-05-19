package merail.life.design

import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val LocalWordMeTypography = staticCompositionLocalOf { UiTypography }
internal val LocalWordMeGameTypography = staticCompositionLocalOf { GameTypography }

internal val Typography.materialTypography: Typography
    get() = this

@OptIn(ExperimentalTextApi::class)
private val interFontFamily = FontFamily(
    Font(
        resId = R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(300),
        ),
        weight = FontWeight.Light,
    ),
    Font(
        resId = R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400),
        ),
        weight = FontWeight.Normal,
    ),
    Font(
        resId = R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500),
        ),
        weight = FontWeight.Medium,
    ),
    Font(
        resId = R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(700),
        ),
        weight = FontWeight.Bold,
    ),
)

private val latoFontFamily = FontFamily(
    fonts = listOf(Font(R.font.lato)),
)

val UiTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 40.0.sp,
        letterSpacing = 0.0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
)

val GameTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = latoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.0.sp,
        letterSpacing = 0.0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = latoFontFamily,
        fontWeight = FontWeight.Normal,
        // Doesn't match with default
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
)
