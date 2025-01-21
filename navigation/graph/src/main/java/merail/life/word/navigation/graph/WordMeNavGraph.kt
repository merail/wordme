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
import merail.life.word.victory.VictoryContainer

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
                onVictory = {
                    navController.navigate(NavigationRoute.Victory)
                }
            )
        }

        dialog<NavigationRoute.Victory>(
            dialogProperties = DialogProperties(
                dismissOnClickOutside = true,
            ),
        ) {
            VictoryContainer(
                onDismiss = {
                    navController.popBackStack()
                },
            )
        }
    }
}