package merail.life.design

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun WordMeTheme(content: @Composable () -> Unit) {
    SystemBarsColor()

    val colors = remember {
        Colors()
    }

    val typography = remember {
        UiTypography
    }

    val gameTypography = remember {
        GameTypography
    }

    CompositionLocalProvider(
        LocalWordMeColors provides colors,
        LocalWordMeTypography provides typography,
        LocalWordMeGameTypography provides gameTypography,
    ) {
        MaterialTheme(
            colorScheme = colors.materialThemeColors,
            typography = typography.materialTypography,
        ) {
            content()
        }
    }
}

object WordMeTheme {
    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalWordMeColors.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalWordMeTypography.current

    val gameTypography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalWordMeGameTypography.current
}

@Composable
private fun SystemBarsColor() {
    val activity = LocalActivity.current
    LocalView.current.run {
        SideEffect {
            activity?.window?.let {
                WindowCompat.getInsetsController(it, it.decorView).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }
}