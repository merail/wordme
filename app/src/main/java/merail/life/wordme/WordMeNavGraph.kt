package merail.life.wordme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import merail.life.connection.NoInternetRoute
import merail.life.connection.noInternetScreen
import merail.life.game.impl.GameRoute
import merail.life.game.impl.gameScreen
import merail.life.game.impl.navigateToGame
import merail.life.result.navigateToResult
import merail.life.result.resultScreen
import merail.life.stats.navigateToStats
import merail.life.stats.statsScreen

@Composable
fun WordMeNavHost(
    navController: NavHostController,
    isNoInternet: Boolean,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = if (isNoInternet) {
            NoInternetRoute
        } else {
            GameRoute
        },
        modifier = modifier,
    ) {
        noInternetScreen(
            navigateToGame = navController::navigateToGame,
        )

        gameScreen(
            navigateToResult = { isVictory, attemptsCount ->
                navController.navigateToResult(
                    isVictory = isVictory,
                    attemptsCount = attemptsCount,
                )
            },
            navigateToStats = navController::navigateToStats,
        )

        resultScreen(
            onDismiss = navController::popBackStack,
        )

        statsScreen(
            onDismiss = navController::popBackStack,
        )
    }
}
