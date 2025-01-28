package merail.life.word.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import merail.life.word.game.GameContainer
import merail.life.word.navigation.domain.NavigationRoute
import merail.life.word.result.ResultContainer
import merail.life.word.stats.StatsContainer

@Composable
fun WordMeNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Game,
        modifier = modifier,
    ) {
        composable<NavigationRoute.Game> {
            GameContainer(
                onResult = { isVictory ->
                    navController.navigate(
                        route = NavigationRoute.Result(
                            isVictory = isVictory,
                        ),
                    )
                },
                onStats = {
                    navController.navigate(NavigationRoute.Stats)
                },
            )
        }

        dialog<NavigationRoute.Result>(
            dialogProperties = DialogProperties(
                dismissOnClickOutside = false,
            ),
        ) {
            ResultContainer(
                onDismiss = {
                    navController.popBackStack()
                },
            )
        }

        dialog<NavigationRoute.Stats>(
            dialogProperties = DialogProperties(
                dismissOnClickOutside = false,
            ),
        ) {
            StatsContainer(
                onDismiss = {
                    navController.popBackStack()
                },
            )
        }
    }
}