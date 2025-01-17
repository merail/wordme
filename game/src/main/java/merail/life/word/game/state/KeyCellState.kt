package merail.life.word.game.state

import merail.life.word.domain.KeyCellStateModel

internal enum class KeyCellState {
    DEFAULT,
    ABSENT,
    PRESENT,
    CORRECT,
    ;
}

internal fun KeyCellStateModel.toUiModel() = when (this) {
    KeyCellStateModel.ABSENT -> KeyCellState.ABSENT
    KeyCellStateModel.PRESENT -> KeyCellState.PRESENT
    KeyCellStateModel.CORRECT -> KeyCellState.CORRECT
    KeyCellStateModel.DEFAULT -> KeyCellState.DEFAULT
}