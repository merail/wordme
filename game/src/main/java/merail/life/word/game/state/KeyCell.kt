package merail.life.word.game.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import merail.life.word.domain.KeyCellModel

internal typealias KeyCellsList = SnapshotStateList<SnapshotStateList<KeyCell>>

internal val emptyKeyField: SnapshotStateList<KeyCell>
    get() = mutableStateListOf(
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY)
    )

internal data class KeyCell(
    val key: Key,
    val state: KeyCellState = KeyCellState.DEFAULT,
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

internal fun KeyCellsList.toModel() = map {
    it.toList().map { keyCell ->
        KeyCellModel(
            value = keyCell.key.value,
            state = keyCell.state.toModel(),
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