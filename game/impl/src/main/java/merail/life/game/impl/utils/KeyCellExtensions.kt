package merail.life.game.impl.utils

import merail.life.domain.KeyCellModel
import merail.life.domain.KeyStateModel
import merail.life.game.impl.ROWS_COUNT
import merail.life.game.impl.model.Key
import merail.life.game.impl.model.KeyCell
import merail.life.game.impl.model.KeyState

internal typealias KeyCellsList = List<List<KeyCell>>

internal val emptyKeyField: List<KeyCell>
    get() = listOf(
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY)
    )

internal val emptyKeyFields: KeyCellsList
    get() = listOf(
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
    )

internal val defaultKeyButtons: KeyCellsList
    get() = listOf(
        listOf(KeyCell(Key.Й), KeyCell(Key.Ц), KeyCell(Key.У), KeyCell(Key.К), KeyCell(Key.Е),
            KeyCell(Key.Н), KeyCell(Key.Г), KeyCell(Key.Ш), KeyCell(Key.Щ), KeyCell(Key.З), KeyCell(Key.Х),
            KeyCell(Key.Ъ)),
        listOf(KeyCell(Key.Ф), KeyCell(Key.Ы), KeyCell(Key.В), KeyCell(Key.А), KeyCell(Key.П),
            KeyCell(Key.Р), KeyCell(Key.О), KeyCell(Key.Л), KeyCell(Key.Д), KeyCell(Key.Ж), KeyCell(Key.Э)),
        listOf(KeyCell(Key.DEL), KeyCell(Key.Я), KeyCell(Key.Ч), KeyCell(Key.С), KeyCell(Key.М),
            KeyCell(Key.И), KeyCell(Key.Т), KeyCell(Key.Ь), KeyCell(Key.Б), KeyCell( Key.Ю), KeyCell(Key.OK)),
        )

internal fun List<List<KeyCellModel>>.toUiModel() = map { keyCellModel ->
    keyCellModel.map { entry ->
        KeyCell(
            key = Key.getKeyFromValue(entry.value),
            state = entry.state.toUiModel(),
        )
    }
}

internal fun KeyCellsList.toLogicModel() = map {
    it.map { keyCell ->
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

internal fun List<KeyCell>.toStringWord(): String {
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