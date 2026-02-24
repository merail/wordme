package merail.life.connection

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import merail.life.core.NavigationRoute
import merail.life.connection.view.NoInternetScreen

@Serializable
data object NoInternetRoute : NavigationRoute

fun NavController.navigateToNoInternet(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = NoInternetRoute,
        builder = navOptions,
    )
}

fun NavGraphBuilder.noInternetScreen(
    navigateToGame: () -> Unit,
) {
    composable<NoInternetRoute> {
        NoInternetScreen(
            onReconnect = navigateToGame,
        )
    }
}
