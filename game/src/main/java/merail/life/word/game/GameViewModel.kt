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
import merail.life.word.game.model.KeyState
import merail.life.word.game.state.CheckWordKeyState
import merail.life.word.game.state.DeleteKeyState
import merail.life.word.game.state.GameResultState
import merail.life.word.game.state.WordCheckState
import merail.life.word.game.utils.defaultKeyButtons
import merail.life.word.game.utils.firstEmptyRow
import merail.life.word.game.utils.isDefeat
import merail.life.word.game.utils.isWin
import merail.life.word.game.utils.orEmpty
import merail.life.word.game.utils.toLogicModel
import merail.life.word.game.utils.toStringWord
import merail.life.word.game.utils.toUiModel
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

    var keyForms = GameStore.keyForms?.toUiModel().orEmpty()
        private set

    private var currentIndex = Pair(
        first = keyForms.firstEmptyRow,
        second = 0,
    )

    var keyButtons = defaultKeyButtons
        private set

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
            keyForms.isDefeat -> gameResultState.value = GameResultState.Defeat
            keyForms.isWin -> gameResultState.value = GameResultState.Victory
        }

        setKeyButtonsStateAfterWordCheck()
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
        if (gameResultState.value.isGameEnd.not()) {
            val rowIndex = currentIndex.first
            val columnIndex = currentIndex.second
            if (columnIndex < COLUMNS_COUNT) {
                keyForms[rowIndex][columnIndex] = keyForms[rowIndex][columnIndex].copy(
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
        if (gameResultState.value.isGameEnd.not()) {
            val rowIndex = currentIndex.first
            val columnIndex = currentIndex.second
            if (columnIndex > 0) {
                keyForms[rowIndex][columnIndex - 1] = keyForms[rowIndex][columnIndex - 1].copy(
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
        if (columnIndex == COLUMNS_COUNT && wordCheckState.value is WordCheckState.None) {
            checkWordKeyState.value = CheckWordKeyState.Loading

            val enteredWord = keyForms[rowIndex].toStringWord()

            if (enteredWord == wordOfTheDay.value) {
                onVictory(rowIndex)
                setKeyButtonsStateAfterWordCheck()
            } else {
                viewModelScope.launch {
                    val isWordExist = databaseRepository.isWordExist(enteredWord)

                    if (isWordExist) {
                        onCorrectWord(
                            enteredWord = enteredWord,
                            rowIndex = rowIndex,
                        )
                        setKeyButtonsStateAfterWordCheck()
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
        setKeyFormsOnCorrectWord(rowIndex)
        wordCheckState.value = WordCheckState.CorrectWord(rowIndex)
        disableControlKeys()
        gameResultState.value = GameResultState.Victory
        saveStats(true)
    }

    private fun onCorrectWord(
        enteredWord: String,
        rowIndex: Int,
    ) {
        setKeyFormsStateOnExistingWord(
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

    private fun setKeyFormsOnCorrectWord(
        rowIndex: Int,
    ) {
        keyForms[rowIndex].forEachIndexed { index, _ ->
            keyForms[rowIndex][index] = keyForms[rowIndex][index].copy(
                state = KeyState.CORRECT,
            )
        }

        saveKeyForms()
    }

    private fun setKeyFormsStateOnExistingWord(
        enteredWord: String,
        rowIndex: Int,
    ) {
        val restCharsList = wordOfTheDay.value.toMutableList()
        keyForms[rowIndex].forEachIndexed { index, _ ->
            keyForms[rowIndex][index] = keyForms[rowIndex][index].copy(
                state = when {
                    enteredWord[index] == wordOfTheDay.value[index] -> {
                        restCharsList.remove(enteredWord[index])
                        KeyState.CORRECT
                    }

                    enteredWord[index] != wordOfTheDay.value[index] -> if (enteredWord[index] in restCharsList) {
                        restCharsList.remove(enteredWord[index])
                        KeyState.PRESENT
                    } else {
                        restCharsList.remove(enteredWord[index])
                        KeyState.ABSENT
                    }

                    else -> KeyState.DEFAULT
                },
            )
        }

        currentIndex = currentIndex.copy(
            first = rowIndex + 1,
            second = 0,
        )

        saveKeyForms()
    }

    private fun setKeyButtonsStateAfterWordCheck() {
        val uniqueKeyForms = keyForms.flatten().distinct()
        uniqueKeyForms.forEach { uniqueKeyForm ->
            keyButtons.forEachIndexed { rowIndex, keyButtonsRow ->
                val columnIndex = keyButtonsRow.indexOfFirst {
                    it.key == uniqueKeyForm.key
                }
                if (columnIndex != -1) {
                    keyButtons[rowIndex][columnIndex] = keyButtons[rowIndex][columnIndex].copy(
                        state = uniqueKeyForm.state,
                    )
                }
            }
        }
    }

    private fun saveKeyForms() = viewModelScope.launch {
        storeRepository.saveKeyForms(keyForms.toLogicModel())
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

