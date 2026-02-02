package merail.life.game.impl

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import merail.life.core.NavigationRoute

@Serializable
data object GameRoute : NavigationRoute

fun NavController.navigateToGame(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = GameRoute,
        builder = navOptions,
    )
}

fun NavGraphBuilder.gameScreen(
    navigateToResult: (Boolean, Int) -> Unit,
    navigateToStats: () -> Unit,
) {
    composable<GameRoute> {
        GameContainer(
            onResult = navigateToResult,
            onStats = navigateToStats,
        )
    }
}
