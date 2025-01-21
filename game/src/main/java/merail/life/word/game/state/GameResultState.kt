package merail.life.word.game.state

internal sealed class GameResultState {
    data object Process: GameResultState()

    data object Defeat: GameResultState()

    data object Victory : GameResultState()
}