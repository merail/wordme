package merail.life.game.impl

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import merail.life.database.api.IDatabaseRepository
import merail.life.domain.Empty
import merail.life.domain.WordModel
import merail.life.domain.constants.IS_TEST_ENVIRONMENT
import merail.life.game.api.IGameRepository
import merail.life.game.impl.model.Key
import merail.life.game.impl.model.KeyCell
import merail.life.game.impl.model.KeyState
import merail.life.game.impl.state.CheckWordKeyState
import merail.life.game.impl.state.DeleteKeyState
import merail.life.game.impl.state.GameResultState
import merail.life.game.impl.state.WordCheckState
import merail.life.game.impl.utils.KeyCellsList
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
import merail.life.time.api.BuildConfig
import merail.life.time.api.ITimeRepository
import javax.inject.Inject

internal const val ROWS_COUNT = 6
internal const val COLUMNS_COUNT = 5
internal const val KEYBOARD_COLUMNS_COUNT = 3

@HiltViewModel
internal class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val databaseRepository: IDatabaseRepository,
    private val storeRepository: IStoreRepository,
    private val timeRepository: ITimeRepository,
    private val gameRepository: IGameRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "GameViewModel"
    }

    var dayWord = WordModel.Empty
        private set

    private val _keyForms = MutableStateFlow(emptyKeyFields)
    val keyForms: StateFlow<KeyCellsList> = _keyForms

    var currentIndex = Pair(
        first = 0,
        second = 0,
    )
        private set

    private val _keyButtons = MutableStateFlow(defaultKeyButtons)
    val keyButtons: StateFlow<KeyCellsList> = _keyButtons

    private val _checkWordKeyState = MutableStateFlow<CheckWordKeyState>(CheckWordKeyState.Disabled)
    val checkWordKeyState: StateFlow<CheckWordKeyState> = _checkWordKeyState

    private val _deleteKeyState = MutableStateFlow<DeleteKeyState>(DeleteKeyState.Disabled)
    val deleteKeyState: StateFlow<DeleteKeyState> = _deleteKeyState

    private val _wordCheckState = MutableStateFlow<WordCheckState>(WordCheckState.None)
    val wordCheckState: StateFlow<WordCheckState> = _wordCheckState

    private val _gameResultState = MutableStateFlow<GameResultState>(GameResultState.Process)
    val gameResultState: StateFlow<GameResultState> = _gameResultState

    private val _timeUntilNextDay = MutableStateFlow("")
    val timeUntilNextDay: StateFlow<String> = _timeUntilNextDay

    private val _isResultBoardVisible = MutableStateFlow(false)
    val isResultBoardVisible: StateFlow<Boolean> = _isResultBoardVisible

    private val _isNextDay = MutableStateFlow(false)
    val isNextDay: StateFlow<Boolean> = _isNextDay

    private val isTestEnvironment = savedStateHandle.get<Boolean>(IS_TEST_ENVIRONMENT) == true

    init {
        if (isTestEnvironment.not()) {
            viewModelScope.launch {
                onLoadValuesStart()
            }

            viewModelScope.launch {
                timeRepository.getTimeUntilNextDay().collect { (time, isNextDay) ->
                    onSecondCount(time, isNextDay)
                }
            }
        }
    }

    @VisibleForTesting
    suspend fun onLoadValuesStart() {
        dayWord = gameRepository.getDayWord().first()

        gameRepository.getKeyForms().first().let {
            _keyForms.value = it.toUiModel().orEmpty()

            currentIndex = Pair(
                first = _keyForms.value.firstEmptyRow,
                second = 0,
            )

            when {
                _keyForms.value.isDefeat -> _gameResultState.value = GameResultState.Defeat
                _keyForms.value.isWin -> _gameResultState.value = GameResultState.Victory
            }
        }
    }

    @VisibleForTesting
    suspend fun onSecondCount(
        time: String,
        isNextDay: Boolean,
    ) {
        _timeUntilNextDay.value = time
        if (isNextDay) {
            if (BuildConfig.REDUCE_TIME_UNTIL_NEXT_DAY) {
                ITimeRepository.countdownStartRealTime = System.currentTimeMillis()
                ITimeRepository.debugDaysSinceStartCount++
            }

            storeRepository.removeKeyForms()
            val daysSinceStartCount = timeRepository.getDaysSinceStartCount().first()
            val dayWordId = databaseRepository.getDayWordId(daysSinceStartCount + 1)
            storeRepository.saveDaysSinceStartCount(daysSinceStartCount)
            dayWord = databaseRepository.getDayWord(dayWordId.value)
            _keyForms.value = emptyKeyFields
            _keyButtons.value = defaultKeyButtons
            _checkWordKeyState.value = CheckWordKeyState.Disabled
            _wordCheckState.value = WordCheckState.None
            _gameResultState.value = GameResultState.Process
            currentIndex = Pair(0, 0)
            _isResultBoardVisible.value = false
        }
        _isNextDay.value = isNextDay
    }

    fun disableControlKeys() {
        _deleteKeyState.value = DeleteKeyState.Disabled
        _checkWordKeyState.value = CheckWordKeyState.Disabled
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
        when (_gameResultState.value) {
            is GameResultState.Process -> disableControlKeys()
            is GameResultState.Victory -> {
                onGameEnd(
                    true,
                    currentIndex.first,
                )
                _isResultBoardVisible.value = true
            }
            is GameResultState.Defeat -> {
                onGameEnd(
                    false,
                    currentIndex.first,
                )
                _isResultBoardVisible.value = true
            }
        }
    }

    private fun addKey(key: Key) {
        if (_gameResultState.value.isGameEnd.not()) {
            val rowIndex = currentIndex.first
            val columnIndex = currentIndex.second
            if (columnIndex < COLUMNS_COUNT) {
                updateKeyFormCell(rowIndex, columnIndex) {
                    it.copy(
                        key = key,
                    )
                }
                currentIndex = currentIndex.copy(
                    second = columnIndex + 1,
                )
                if (columnIndex == COLUMNS_COUNT - 1) {
                    _checkWordKeyState.value = CheckWordKeyState.Enabled
                } else {
                    _checkWordKeyState.value = CheckWordKeyState.Disabled
                }
                _deleteKeyState.value = DeleteKeyState.Enabled
                _wordCheckState.value = WordCheckState.None
            }
        }
    }

    private fun removeKey() {
        if (_gameResultState.value.isGameEnd.not()) {
            val rowIndex = currentIndex.first
            val columnIndex = currentIndex.second
            if (columnIndex > 0) {
                updateKeyFormCell(rowIndex, columnIndex - 1) {
                    it.copy(
                        key = Key.EMPTY,
                    )
                }
                currentIndex = currentIndex.copy(
                    second = columnIndex - 1,
                )
                if (columnIndex - 1 == 0) {
                    _deleteKeyState.value = DeleteKeyState.Disabled
                } else {
                    _deleteKeyState.value = DeleteKeyState.Enabled
                }
                _checkWordKeyState.value = CheckWordKeyState.Disabled
                _wordCheckState.value = WordCheckState.None
            }
        }
    }

    private fun checkWord() {
        val rowIndex = currentIndex.first
        val columnIndex = currentIndex.second
        if (columnIndex == COLUMNS_COUNT && _wordCheckState.value is WordCheckState.None) {
            _checkWordKeyState.value = CheckWordKeyState.Loading

            val enteredWord = _keyForms.value[rowIndex].toStringWord()

            if (enteredWord == dayWord.value) {
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
        val uniqueKeyForms = _keyForms.value
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

        var newKeyButtons = _keyButtons.value
        uniqueKeyForms.forEach { uniqueKeyForm ->
            newKeyButtons = newKeyButtons.mapIndexed { _, keyButtonsRow ->
                val columnIndex = keyButtonsRow.indexOfFirst {
                    it.key == uniqueKeyForm.key
                }
                if (columnIndex != -1) {
                    keyButtonsRow.mapIndexed { c, cell ->
                        if (c == columnIndex) {
                            cell.copy(
                                state = uniqueKeyForm.state,
                            )
                        } else {
                            cell
                        }
                    }
                } else {
                    keyButtonsRow
                }
            }
        }
        _keyButtons.value = newKeyButtons
    }

    private fun onVictory(
        rowIndex: Int,
    ) {
        setKeyFormsOnCorrectWord(rowIndex)
        _wordCheckState.value = WordCheckState.CorrectWord(rowIndex)
        disableControlKeys()
        _gameResultState.value = GameResultState.Victory
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
        _wordCheckState.value = WordCheckState.ExistingWord(rowIndex)
        disableControlKeys()
        if (rowIndex + 1 == ROWS_COUNT) {
            onDefeat()
        }
    }

    private fun onWrongWord(
        rowIndex: Int,
    ) {
        _wordCheckState.value = WordCheckState.NonExistentWord(rowIndex)
        _checkWordKeyState.value = CheckWordKeyState.Disabled
    }

    private fun onDefeat() {
        _gameResultState.value = GameResultState.Defeat
        saveStats(false)
    }

    private fun setKeyFormsOnCorrectWord(
        rowIndex: Int,
    ) {
        updateKeyFormRow(rowIndex) { _, cell ->
            cell.copy(
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
        val restCharsList = dayWord.value.toMutableList()

        updateKeyFormRow(rowIndex) { index, cell ->
            cell.copy(
                state = when {
                    enteredWord[index] == dayWord.value[index] -> {
                        restCharsList.remove(enteredWord[index])
                        KeyState.CORRECT
                    }
                    else -> KeyState.ABSENT
                },
            )
        }

        _keyForms.value = _keyForms.value.mapIndexed { r, rowList ->
            if (r == rowIndex) rowList.mapIndexed { index, cell ->
                if (cell.state != KeyState.CORRECT) {
                    cell.copy(
                        state = when {
                            enteredWord[index] in restCharsList -> {
                                restCharsList.remove(enteredWord[index])
                                KeyState.PRESENT
                            }
                            else -> KeyState.ABSENT
                        },
                    )
                } else {
                    cell
                }
            } else rowList
        }

        currentIndex = currentIndex.copy(
            first = rowIndex + 1,
            second = 0,
        )

        saveKeyForms()
    }

    private fun saveKeyForms() = viewModelScope.launch {
        storeRepository.saveKeyForms(_keyForms.value.toLogicModel())
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
            storeRepository.updateStatsOnDefeat()
        }
    }

    private fun updateKeyFormCell(row: Int, col: Int, transform: (KeyCell) -> KeyCell) {
        _keyForms.value = _keyForms.value.mapIndexed { r, rowList ->
            if (r == row) rowList.mapIndexed { c, cell ->
                if (c == col) {
                    transform(cell)
                } else {
                    cell
                }
            } else {
                rowList
            }
        }
    }

    private fun updateKeyFormRow(row: Int, transform: (Int, KeyCell) -> KeyCell) {
        _keyForms.value = _keyForms.value.mapIndexed { r, rowList ->
            if (r == row) {
                rowList.mapIndexed { c, cell ->
                    transform(c, cell)
                }
            } else {
                rowList
            }
        }
    }
}