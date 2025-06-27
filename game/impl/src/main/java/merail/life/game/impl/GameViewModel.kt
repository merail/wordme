package merail.life.game.impl

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import merail.life.core.BuildConfig
import merail.life.database.api.IDatabaseRepository
import merail.life.domain.WordModel
import merail.life.game.api.IGameRepository
import merail.life.game.impl.model.Key
import merail.life.game.impl.model.KeyCell
import merail.life.game.impl.model.KeyState
import merail.life.game.impl.state.CheckWordKeyState
import merail.life.game.impl.state.DeleteKeyState
import merail.life.game.impl.state.GameResultState
import merail.life.game.impl.state.WordCheckState
import merail.life.game.impl.utils.defaultKeyButtons
import merail.life.game.impl.utils.emptyKeyFields
import merail.life.game.impl.utils.firstEmptyRow
import merail.life.game.impl.utils.isDefeat
import merail.life.game.impl.utils.isWin
import merail.life.game.impl.utils.orEmpty
import merail.life.game.impl.utils.toLogicModel
import merail.life.game.impl.utils.toStringWord
import merail.life.game.impl.utils.toUiModel
import merail.life.store.api.IStoreRepository
import merail.life.time.api.ITimeRepository
import javax.inject.Inject

internal const val ROWS_COUNT = 6
internal const val COLUMNS_COUNT = 5
internal const val KEYBOARD_COLUMNS_COUNT = 3

@HiltViewModel
internal class GameViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository,
    private val storeRepository: IStoreRepository,
    private val timeRepository: ITimeRepository,
    private val gameRepository: IGameRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "GameViewModel"
    }

    private var dayWord: WordModel?
        get() = gameRepository.getDayWord().value
        set(value) = gameRepository.setDayWord(value)

    var keyForms = emptyKeyFields
        private set

    var currentIndex = Pair(
        first = keyForms.firstEmptyRow,
        second = 0,
    )
        private set

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

    var timeUntilNextDay by mutableStateOf("")
        private set

    var isResultBoardVisible by mutableStateOf(false)
        private set

    var isNextDay by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            gameRepository.getKeyForms().collect {
                keyForms.clear()
                keyForms.addAll(it?.toUiModel().orEmpty())

                currentIndex = Pair(
                    first = keyForms.firstEmptyRow,
                    second = 0,
                )

                when {
                    keyForms.isDefeat -> gameResultState.value = GameResultState.Defeat
                    keyForms.isWin -> gameResultState.value = GameResultState.Victory
                }
            }
        }

        viewModelScope.launch {
            startNextDayTimer()
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

    fun onFlipAnimationEnd(
        onGameEnd: (Boolean, Int) -> Unit,
    ) {
        setKeyButtonsStateAfterWordCheck()
        when (gameResultState.value) {
            is GameResultState.Process -> disableControlKeys()
            is GameResultState.Victory -> {
                onGameEnd(
                    true,
                    currentIndex.first,
                )
                isResultBoardVisible = true
            }
            is GameResultState.Defeat -> {
                onGameEnd(
                    false,
                    currentIndex.first,
                )
                isResultBoardVisible = true
            }
        }
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

            if (enteredWord == dayWord?.value) {
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

    private fun setKeyButtonsStateAfterWordCheck() {
        val uniqueKeyForms = keyForms
            .flatten()
            .distinct()
            .groupBy(KeyCell::key)
            .map { (_, group) ->
                group.find {
                    it.state == KeyState.CORRECT
                } ?: group.find {
                    it.state == KeyState.PRESENT
                } ?: group.first()
            }

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

    private fun onVictory(
        rowIndex: Int,
    ) {
        setKeyFormsOnCorrectWord(rowIndex)
        wordCheckState.value = WordCheckState.CorrectWord(rowIndex)
        disableControlKeys()
        gameResultState.value = GameResultState.Victory
        saveLastVictoryDay()
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

        currentIndex = currentIndex.copy(
            first = rowIndex + 1,
            second = 0,
        )

        saveKeyForms()
    }

    private fun setKeyFormsStateOnExistingWord(
        enteredWord: String,
        rowIndex: Int,
    ) {
        val restCharsList = dayWord?.value.orEmpty().toMutableList()

        keyForms[rowIndex].forEachIndexed { index, _ ->
            keyForms[rowIndex][index] = keyForms[rowIndex][index].copy(
                state = when {
                    enteredWord[index] == dayWord?.value[index] -> {
                        restCharsList.remove(enteredWord[index])
                        KeyState.CORRECT
                    }

                    else -> KeyState.ABSENT
                },
            )
        }

        keyForms[rowIndex].forEachIndexed { index, _ ->
            if (keyForms[rowIndex][index].state != KeyState.CORRECT) {
                keyForms[rowIndex][index] = keyForms[rowIndex][index].copy(
                    state = when {
                        enteredWord[index] in restCharsList -> {
                            restCharsList.remove(enteredWord[index])
                            KeyState.PRESENT
                        }

                        else -> KeyState.ABSENT
                    },
                )
            }
        }

        currentIndex = currentIndex.copy(
            first = rowIndex + 1,
            second = 0,
        )

        saveKeyForms()
    }

    private fun saveKeyForms() = viewModelScope.launch {
        storeRepository.saveKeyForms(keyForms.toLogicModel())
    }

    private fun saveLastVictoryDay() = viewModelScope.launch {
        val daysSinceStartCount = timeRepository.getDaysSinceStartCount().first()
        storeRepository.saveLastVictoryDay(daysSinceStartCount)
    }

    private fun saveStats(isVictory: Boolean) = viewModelScope.launch {
        if (isVictory) {
            storeRepository.updateStatsOnVictory(
                attemptsCount = currentIndex.first,
            )
        } else {
            storeRepository.updateStatsOnOnDefeat()
        }
    }

    private suspend fun startNextDayTimer() {
        while (isNextDay.not()) {
            val (time, isNextDay) = timeRepository.getTimeUntilNextDay()
            timeUntilNextDay = time
            if (isNextDay) {
                if (BuildConfig.REDUCE_TIME_UNTIL_NEXT_DAY) {
                    ITimeRepository.countdownStartRealTime = System.currentTimeMillis()
                    ITimeRepository.debugDaysSinceStartCount++
                }

                storeRepository.removeKeyForms()
                val daysSinceStartCount = timeRepository.getDaysSinceStartCount().first()
                val dayWordId = databaseRepository.getDayWordId(daysSinceStartCount + 1)
                dayWord = databaseRepository.getDayWord(dayWordId.value)
                keyForms = emptyKeyFields
                keyButtons = defaultKeyButtons
                checkWordKeyState.value = CheckWordKeyState.Disabled
                wordCheckState.value = WordCheckState.None
                gameResultState.value = GameResultState.Process
                currentIndex = Pair(0, 0)
                isResultBoardVisible = false

                this@GameViewModel.isNextDay = true
            }
            delay(1000L)
        }

        isNextDay = false

        startNextDayTimer()
    }
}

