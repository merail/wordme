package merail.life.game.impl.model

import merail.life.design.components.LetterCellState

internal enum class KeyState {
    DEFAULT,
    ABSENT,
    PRESENT,
    CORRECT,
    ;
}

internal fun KeyState.toLetterCellState() = when (this) {
    KeyState.DEFAULT -> LetterCellState.DEFAULT
    KeyState.ABSENT -> LetterCellState.ABSENT
    KeyState.PRESENT -> LetterCellState.PRESENT
    KeyState.CORRECT -> LetterCellState.CORRECT
}