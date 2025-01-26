package merail.life.word.game

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import merail.life.word.database.api.IDatabaseRepository
import merail.life.word.domain.GameStore
import merail.life.word.domain.orEmpty
import merail.life.word.game.model.Key
import merail.life.word.game.model.KeyCellState
import merail.life.word.game.model.isDefeat
import merail.life.word.game.model.isWin
import merail.life.word.game.model.firstEmptyRow
import merail.life.word.game.model.orEmpty
import merail.life.word.game.model.toLogicModel
import merail.life.word.game.model.toUiModel
import merail.life.word.game.model.toStringWord
import merail.life.word.game.state.CheckWordKeyState
import merail.life.word.game.state.DeleteKeyState
import merail.life.word.game.state.GameResultState
import merail.life.word.game.state.WordCheckState
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
        first = keyFields.firstEmptyRow,
        second = 0,
    )

    var checkWordKeyState = mutableStateOf<CheckWordKeyState>(CheckWordKeyState.Disabled)
        private set

    var deleteKeyState = mutableStateOf<DeleteKeyState>(DeleteKeyState.Disabled)
        private set

    var wordCheckState = mutableStateOf<WordCheckState>(WordCheckState.None)
        private set

    var gameResultState = mutableStateOf<GameResultState>(GameResultState.Process)
        private set

    init {
        when {
            keyFields.isDefeat -> gameResultState.value = GameResultState.Defeat
            keyFields.isWin -> gameResultState.value = GameResultState.Victory
        }
    }

    fun disableControlKeys() {
        deleteKeyState.value = DeleteKeyState.Disabled
        checkWordKeyState.value = CheckWordKeyState.Disabled
    }

    fun handleKeyClick(key: Key) = when (key) {
        Key.DEL -> removeKey()
        Key.OK -> checkWord()
        else -> addKey(key)
    }

    private fun addKey(key: Key) {
        if (gameResultState.value.isEnd.not()) {
            val rowIndex = currentIndex.first
            val columnIndex = currentIndex.second
            if (columnIndex < COLUMNS_COUNT) {
                keyFields[rowIndex][columnIndex] = keyFields[rowIndex][columnIndex].copy(
                    key = key,
                )
                currentIndex = currentIndex.copy(
                    second = columnIndex + 1,
                )
                if (columnIndex == COLUMNS_COUNT - 1) {
                    checkWordKeyState.value = CheckWordKeyState.Enabled
                } else {
                    checkWordKeyState.value = CheckWordKeyState.Disabled
                }
                deleteKeyState.value = DeleteKeyState.Enabled
                wordCheckState.value = WordCheckState.None
            }
        }
    }

    private fun removeKey() {
        if (gameResultState.value.isEnd.not()) {
            val rowIndex = currentIndex.first
            val columnIndex = currentIndex.second
            if (columnIndex > 0) {
                keyFields[rowIndex][columnIndex - 1] = keyFields[rowIndex][columnIndex - 1].copy(
                    key = Key.EMPTY,
                )
                currentIndex = currentIndex.copy(
                    second = columnIndex - 1,
                )
                if (columnIndex - 1 == 0) {
                    deleteKeyState.value = DeleteKeyState.Disabled
                } else {
                    deleteKeyState.value = DeleteKeyState.Enabled
                }
                checkWordKeyState.value = CheckWordKeyState.Disabled
                wordCheckState.value = WordCheckState.None
            }
        }
    }

    private fun checkWord() {
        val rowIndex = currentIndex.first
        val columnIndex = currentIndex.second
        if (columnIndex == COLUMNS_COUNT) {
            checkWordKeyState.value = CheckWordKeyState.Loading

            val enteredWord = keyFields[rowIndex].toStringWord()

            if (enteredWord == wordOfTheDay.value) {
                onVictory(rowIndex)
            } else {
                viewModelScope.launch {
                    val isWordExist = databaseRepository.isWordExist(enteredWord)

                    if (isWordExist) {
                        onCorrectWord(
                            enteredWord = enteredWord,
                            rowIndex = rowIndex,
                        )
                    } else {
                        onWrongWord(rowIndex)
                    }
                }
            }
        }
    }

    private fun onVictory(
        rowIndex: Int,
    ) {
        setKeyCellsOnCorrectWord(rowIndex)
        wordCheckState.value = WordCheckState.CorrectWord(rowIndex)
        disableControlKeys()
        gameResultState.value = GameResultState.Victory
        saveStats(true)
    }

    private fun onCorrectWord(
        enteredWord: String,
        rowIndex: Int,
    ) {
        setKeyCellsStateOnExistingWord(
            enteredWord = enteredWord,
            rowIndex = rowIndex,
        )
        wordCheckState.value = WordCheckState.ExistingWord(rowIndex)
        disableControlKeys()
        if (rowIndex + 1 == ROWS_COUNT) {
            onDefeat()
        }
    }

    private fun onWrongWord(
        rowIndex: Int,
    ) {
        wordCheckState.value = WordCheckState.NonExistentWord(rowIndex)
        checkWordKeyState.value = CheckWordKeyState.Disabled
    }

    private fun onDefeat() {
        gameResultState.value = GameResultState.Defeat
        saveStats(false)
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
        storeRepository.saveKeyCells(keyFields.toLogicModel())
    }

    private fun saveStats(isVictory: Boolean) {
        viewModelScope.launch {
            if (isVictory) {
                storeRepository.updateStatsOnVictory(
                    attemptsCount = currentIndex.first + 1,
                )
            } else {
                storeRepository.updateStatsOnOnDefeat()
            }
        }
    }
}

