package merail.life.word.game.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import merail.life.word.domain.KeyCellModel
import merail.life.word.game.ROWS_COUNT

internal typealias KeyCellsList = SnapshotStateList<SnapshotStateList<KeyCell>>

internal val emptyKeyField: SnapshotStateList<KeyCell>
    get() = mutableStateListOf(
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY)
    )

internal fun List<List<KeyCellModel>>.toUiModel() = KeyCellsList().apply {
    this@toUiModel.forEach { keyCellModel ->
        add(
            element = keyCellModel.map { entry ->
                KeyCell(
                    key = Key.getKeyFromValue(entry.value),
                    state = entry.state.toUiModel(),
                )
            }.toMutableStateList(),
        )
    }
}

internal fun KeyCellsList.toLogicModel() = map {
    it.toList().map { keyCell ->
        KeyCellModel(
            value = keyCell.key.value,
            state = keyCell.state.toLogicModel(),
        )
    }
}

internal fun KeyCellsList?.orEmpty() = if (isNullOrEmpty()) {
    mutableStateListOf(
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
    )
} else {
    this
}

internal val KeyCellsList.firstEmptyRow: Int
    get() = indexOfFirst { keyField ->
        keyField.all { keyCell ->
            keyCell.key == Key.EMPTY
        }
    }.let {
        if (it == -1) {
            ROWS_COUNT - 1
        } else {
            it
        }
    }

internal val KeyCellsList.lastFilledRow: Int
    get() = indexOfFirst { keyField ->
        keyField.all { keyCell ->
            keyCell.key == Key.EMPTY
        }
    }.let {
        when(it) {
            -1 -> ROWS_COUNT - 1
            0 -> 0
            else -> it - 1
        }
    }

internal fun SnapshotStateList<KeyCell>.toStringWord(): String {
    var enteredWord = ""
    forEach {
        enteredWord += it.key.value.lowercase()
    }
    return enteredWord
}

internal val KeyCellsList.isWin: Boolean
    get() = this[lastFilledRow].all {
        it.state == KeyCellState.CORRECT
    }

internal val KeyCellsList.isDefeat: Boolean
    get() = isWin.not() && lastFilledRow == ROWS_COUNT - 1