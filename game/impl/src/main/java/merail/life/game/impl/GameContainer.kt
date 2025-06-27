package merail.life.game.impl

import androidx.compose.runtime.Composable
import merail.life.game.impl.view.GameScreen

@Composable
fun GameContainer(
    onResult: (isVictory: Boolean, attemptsCount: Int) -> Unit,
    onStats: () -> Unit,
) = GameScreen(
    onGameEnd = onResult,
    onInfoClick = onStats,
)