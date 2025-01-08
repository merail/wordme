package merail.life.word.game

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
        mutableStateListOf(Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY),
        mutableStateListOf(Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY),
        mutableStateListOf(Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY),
        mutableStateListOf(Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY),
        mutableStateListOf(Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY),
        mutableStateListOf(Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY),
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
            keyFields[rowIndex][columnIndex] = key
            currentIndex = currentIndex.copy(
                second = columnIndex + 1,
            )
        }
    }

    private fun removeKey() {
        val rowIndex = currentIndex.first
        val columnIndex = currentIndex.second
        if (columnIndex > 0) {
            keyFields[rowIndex][columnIndex - 1] = Key.EMPTY
            currentIndex = currentIndex.copy(
                second = columnIndex - 1,
            )
            wordCheckState.value = WordCheckState.None
        }
    }

    private fun checkWord() {
        val rowIndex = currentIndex.first
        val columnIndex = currentIndex.second
        if (columnIndex == COLUMNS_COUNT) {
            var enteredWord = ""

            keyFields[rowIndex].forEach {
                enteredWord += it.value.lowercase()
            }

            if (enteredWord == currentWord.value) {

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

