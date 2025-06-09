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
        Typography
    }

    CompositionLocalProvider(
        LocalWordMeColors provides colors,
        LocalWordMeTypography provides typography,
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