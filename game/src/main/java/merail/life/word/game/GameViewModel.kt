package merail.life.word.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import merail.life.word.database.api.IDatabaseRepository
import merail.life.word.domain.GameStore
import merail.life.word.domain.orEmpty
import merail.life.word.game.state.GameResultState
import merail.life.word.game.state.Key
import merail.life.word.game.state.KeyCellState
import merail.life.word.game.state.WordCheckState
import merail.life.word.game.state.orEmpty
import merail.life.word.game.state.toModel
import merail.life.word.game.state.toUiModel
import merail.life.word.store.api.IStoreRepository
import javax.inject.Inject

internal const val ROWS_COUNT = 6
internal const val COLUMNS_COUNT = 5
internal const val KEYBOARD_COLUMNS_COUNT = 3

@HiltViewModel
internal class GameViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository,
    private val storeRepository: IStoreRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "GameViewModel"
    }

    private var wordOfTheDay = GameStore.wordOfTheDay.orEmpty()

    var keyFields = GameStore.keyCells?.toUiModel().orEmpty()
        private set

    private var currentIndex = Pair(
        first = keyFields.indexOfFirst { keyField ->
            keyField.all { keyCell ->
                keyCell.key == Key.EMPTY
            }
        },
        second = 0,
    )

    var wordCheckState: WordCheckState by mutableStateOf(WordCheckState.None)
        private set

    var gameState: GameResultState by mutableStateOf(GameResultState.Process)
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
            wordCheckState = WordCheckState.None
        }
    }

    private fun removeKey() {
        if (wordCheckState.isWin.not()) {
            val rowIndex = currentIndex.first
            val columnIndex = currentIndex.second
            if (columnIndex > 0) {
                keyFields[rowIndex][columnIndex - 1] = keyFields[rowIndex][columnIndex - 1].copy(
                    key = Key.EMPTY,
                )
                currentIndex = currentIndex.copy(
                    second = columnIndex - 1,
                )
                wordCheckState = WordCheckState.None
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

            if (enteredWord == wordOfTheDay.value) {
                setKeyCellsOnCorrectWord(
                    rowIndex = rowIndex,
                )
                wordCheckState = WordCheckState.CorrectWord(rowIndex)
                gameState = GameResultState.Victory
            } else {
                viewModelScope.launch {
                    val isWordExist = databaseRepository.isWordExist(enteredWord)

                    if (isWordExist) {
                        setKeyCellsStateOnExistingWord(
                            enteredWord = enteredWord,
                            rowIndex = rowIndex,
                        )
                        wordCheckState = WordCheckState.ExistingWord(rowIndex)
                        if (rowIndex == ROWS_COUNT) {
                            gameState = GameResultState.Defeat
                        }
                    } else {
                        wordCheckState = WordCheckState.NonExistentWord(rowIndex)
                    }
                }
            }
        }
    }

    private fun setKeyCellsOnCorrectWord(
        rowIndex: Int,
    ) {
        keyFields[rowIndex].forEachIndexed { index, _ ->
            keyFields[rowIndex][index] = keyFields[rowIndex][index].copy(
                state = KeyCellState.CORRECT,
            )
        }

        saveKeyCells()
    }

    private fun setKeyCellsStateOnExistingWord(
        enteredWord: String,
        rowIndex: Int,
    ) {
        val restCharsList = wordOfTheDay.value.toMutableList()
        keyFields[rowIndex].forEachIndexed { index, _ ->
            keyFields[rowIndex][index] = keyFields[rowIndex][index].copy(
                state = when {
                    enteredWord[index] == wordOfTheDay.value[index] -> {
                        restCharsList.remove(enteredWord[index])
                        KeyCellState.CORRECT
                    }

                    enteredWord[index] != wordOfTheDay.value[index] -> if (enteredWord[index] in restCharsList) {
                        restCharsList.remove(enteredWord[index])
                        KeyCellState.PRESENT
                    } else {
                        restCharsList.remove(enteredWord[index])
                        KeyCellState.ABSENT
                    }

                    else -> KeyCellState.DEFAULT
                },
            )
        }

        currentIndex = currentIndex.copy(
            first = rowIndex + 1,
            second = 0,
        )

        saveKeyCells()
    }

    private fun saveKeyCells() = viewModelScope.launch {
        storeRepository.saveKeyCells(keyFields.toModel())
    }
}

