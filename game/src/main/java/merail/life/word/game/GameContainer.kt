package merail.life.word.game

import androidx.compose.runtime.Composable
import merail.life.word.game.view.GameScreen

@Composable
fun GameContainer(
    onResult: (isVictory: Boolean, attemptsCount: Int) -> Unit,
    onStats: () -> Unit,
) = GameScreen(
    onGameEnd = onResult,
    onInfoClick = onStats,
)