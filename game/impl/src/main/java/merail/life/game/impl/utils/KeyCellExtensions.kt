package merail.life.game.impl.utils

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import merail.life.domain.KeyCellModel
import merail.life.domain.KeyStateModel
import merail.life.game.impl.ROWS_COUNT
import merail.life.game.impl.model.Key
import merail.life.game.impl.model.KeyCell
import merail.life.game.impl.model.KeyState

internal typealias KeyCellsList = SnapshotStateList<SnapshotStateList<KeyCell>>

internal val emptyKeyField: SnapshotStateList<KeyCell>
    get() = mutableStateListOf(
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY)
    )

internal val emptyKeyFields: KeyCellsList
    get() = mutableStateListOf(
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
    )

internal val defaultKeyButtons: KeyCellsList
    get() = mutableStateListOf(
        mutableStateListOf(KeyCell(Key.А), KeyCell(Key.Б), KeyCell(Key.В), KeyCell(Key.Г), KeyCell(Key.Д),
            KeyCell(Key.Е), KeyCell(Key.Ж), KeyCell(Key.З), KeyCell(Key.И), KeyCell(Key.Й), KeyCell(Key.К),
            KeyCell(Key.Л)),
        mutableStateListOf(KeyCell(Key.М), KeyCell(Key.Н), KeyCell(Key.О), KeyCell(Key.П), KeyCell(Key.Р),
            KeyCell(Key.С), KeyCell(Key.Т), KeyCell(Key.У), KeyCell(Key.Ф), KeyCell(Key.Х), KeyCell(Key.Ц)),
        mutableStateListOf(KeyCell(Key.DEL), KeyCell(Key.Ч), KeyCell(Key.Ш), KeyCell(Key.Щ), KeyCell(Key.Ъ),
            KeyCell(Key.Ы), KeyCell(Key.Ь), KeyCell(Key.Э), KeyCell(Key.Ю), KeyCell( Key.Я), KeyCell(Key.OK)),
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

internal fun KeyStateModel.toUiModel() = when (this) {
    KeyStateModel.ABSENT -> KeyState.ABSENT
    KeyStateModel.PRESENT -> KeyState.PRESENT
    KeyStateModel.CORRECT -> KeyState.CORRECT
    KeyStateModel.DEFAULT -> KeyState.DEFAULT
}

internal fun KeyState.toLogicModel() = when (this) {
    KeyState.ABSENT -> KeyStateModel.ABSENT
    KeyState.PRESENT -> KeyStateModel.PRESENT
    KeyState.CORRECT -> KeyStateModel.CORRECT
    KeyState.DEFAULT -> KeyStateModel.DEFAULT
}

internal fun KeyCellsList?.orEmpty() = if (isNullOrEmpty()) {
    emptyKeyFields
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
            ROWS_COUNT
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
        it.state == KeyState.CORRECT
    }

internal val KeyCellsList.isDefeat: Boolean
    get() = isWin.not() && lastFilledRow == ROWS_COUNT - 1

internal val KeyCell.isControlKey: Boolean
    get() = key in listOf(Key.DEL, Key.OK)

internal val KeyCell.isValid: Boolean
    get() = state in listOf(KeyState.PRESENT, KeyState.CORRECT)