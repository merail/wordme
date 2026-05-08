package merail.life.result

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import merail.life.core.NavigationRoute
import merail.life.result.view.ResultScreen

@Serializable
data class ResultRoute(
    val isVictory: Boolean = true,
    val attemptsCount: Int = 4,
) : NavigationRoute

fun NavController.navigateToResult(
    isVictory: Boolean,
    attemptsCount: Int,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = ResultRoute(
            isVictory = isVictory,
            attemptsCount = attemptsCount,
        ),
        builder = {
            launchSingleTop = true
            navOptions()
        },
    )
}

fun NavGraphBuilder.resultScreen(
    onDismiss: () -> Unit,
) {
    dialog<ResultRoute>(
        dialogProperties = DialogProperties(
            dismissOnClickOutside = false,
        ),
    ) {
        ResultScreen(
            onDismiss = onDismiss,
        )
    }
}
