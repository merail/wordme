package merail.life.word.game.state

import androidx.compose.runtime.Stable

@Stable
internal sealed class GameResultState {
    val isEnd: Boolean
        get() = this is Defeat || this is Victory

    data object Process: GameResultState()

    data object Defeat: GameResultState()

    data object Victory : GameResultState()
}