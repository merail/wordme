package merail.life.stats

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import merail.life.core.NavigationRoute

@Serializable
data object StatsRoute : NavigationRoute

fun NavController.navigateToStats(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = StatsRoute,
        builder = navOptions,
    )
}

fun NavGraphBuilder.statsScreen(
    onDismiss: () -> Unit,
) {
    dialog<StatsRoute>(
        dialogProperties = DialogProperties(
            dismissOnClickOutside = false,
        ),
    ) {
        StatsContainer(
            onDismiss = onDismiss,
        )
    }
}
