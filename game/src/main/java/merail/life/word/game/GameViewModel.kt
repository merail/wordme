package merail.life.word.game

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import merail.life.word.database.api.IDatabaseRepository
import merail.life.word.database.api.model.WordModel
import merail.life.word.game.state.WordCheckState
import javax.inject.Inject

internal const val ROWS_COUNT = 6
internal const val COLUMNS_COUNT = 5
internal const val KEYBOARD_COLUMNS_COUNT = 3

internal val emptyKeyField: SnapshotStateList<KeyCell>
    get() = mutableStateListOf(
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
        KeyCell(Key.EMPTY),
    )

@HiltViewModel
internal class GameViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "GameViewModel"
    }

    private var currentWord = WordModel("")

    private var currentIndex = Pair(0, 0)

    init {
        viewModelScope.launch {
            currentWord = databaseRepository.getCurrentWord(213)
        }
    }

    var keyFields = mutableStateListOf(
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
        emptyKeyField,
    )

        private set

    var wordCheckState: MutableState<WordCheckState> = mutableStateOf(WordCheckState.None)
        private set

    fun handleKeyClick(key: Key) = when (key) {
        Key.DEL -> removeKey()
        Key.OK -> checkWord()
        else -> addKey(key)
    }

    private fun addKey(key: Key) {
        val rowIndex = currentIndex.first
        val columnIndex = currentIndex.second
        if (columnIndex < COLUMNS_COUNT) {
            keyFields[rowIndex][columnIndex] = keyFields[rowIndex][columnIndex].copy(
                key = key,
            )
            currentIndex = currentIndex.copy(
                second = columnIndex + 1,
            )
        }
    }

    private fun removeKey() {
        if (wordCheckState.value.isWin.not()) {
            val rowIndex = currentIndex.first
            val columnIndex = currentIndex.second
            if (columnIndex > 0) {
                keyFields[rowIndex][columnIndex - 1] = keyFields[rowIndex][columnIndex - 1].copy(
                    key = Key.EMPTY,
                )
                currentIndex = currentIndex.copy(
                    second = columnIndex - 1,
                )
                wordCheckState.value = WordCheckState.None
            }
        }
    }

    private fun checkWord() {
        val rowIndex = currentIndex.first
        val columnIndex = currentIndex.second
        if (columnIndex == COLUMNS_COUNT) {
            var enteredWord = ""

            keyFields[rowIndex].forEach {
                enteredWord += it.key.value.lowercase()
            }

            if (enteredWord == currentWord.value) {
                keyFields[rowIndex].forEachIndexed { index, _ ->
                    keyFields[rowIndex][index] = keyFields[rowIndex][index].copy(
                        state = KeyCellState.CORRECT,
                    )
                }
                wordCheckState.value = WordCheckState.CorrectWord(rowIndex)
            } else {
                viewModelScope.launch {
                    val isWordExist = databaseRepository.isWordExist(enteredWord)
                    if (isWordExist) {
                        currentIndex = currentIndex.copy(
                            first = rowIndex + 1,
                            second = 0,
                        )
                        wordCheckState.value = WordCheckState.ExistingWord(rowIndex)
                    } else {
                        wordCheckState.value = WordCheckState.NonExistentWord(rowIndex)
                    }
                }
            }
        }
    }
}

