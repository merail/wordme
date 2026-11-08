package merail.life.rules

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import merail.life.core.NavigationRoute
import merail.life.rules.view.RulesScreen

@Serializable
data object RulesRoute : NavigationRoute

fun NavController.navigateToRules(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = RulesRoute,
        builder = {
            launchSingleTop = true
            navOptions()
        },
    )
}

fun NavGraphBuilder.rulesScreen(
    onDismiss: () -> Unit,
) {
    dialog<RulesRoute>(
        dialogProperties = DialogProperties(
            dismissOnClickOutside = false,
        ),
    ) {
        RulesScreen(
            onDismiss = onDismiss,
        )
    }
}
