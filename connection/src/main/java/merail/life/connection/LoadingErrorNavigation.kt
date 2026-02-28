package merail.life.connection

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import merail.life.core.NavigationRoute
import merail.life.connection.view.LoadingErrorScreen

@Serializable
data object LoadingErrorRoute : NavigationRoute

fun NavController.navigateToLoadingError(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = LoadingErrorRoute,
        builder = navOptions,
    )
}

fun NavGraphBuilder.loadingErrorScreen(
    navigateToGame: () -> Unit,
) {
    composable<LoadingErrorRoute> {
        LoadingErrorScreen(
            onReconnect = navigateToGame,
        )
    }
}
