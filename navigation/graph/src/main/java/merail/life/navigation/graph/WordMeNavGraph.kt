package merail.life.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import merail.life.game.GameContainer
import merail.life.wordme.navigation.domain.NavigationRoute
import merail.life.result.ResultContainer
import merail.life.stats.StatsContainer

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
                onResult = { isVictory, attemptsCount ->
                    navController.navigate(
                        route = NavigationRoute.Result(
                            isVictory = isVictory,
                            attemptsCount = attemptsCount,
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