package merail.life.wordme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import merail.life.connection.LoadingErrorRoute
import merail.life.connection.loadingErrorScreen
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
    isLoadingError: Boolean,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoadingError) {
            LoadingErrorRoute
        } else {
            GameRoute
        },
        modifier = modifier,
    ) {
        loadingErrorScreen(
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
