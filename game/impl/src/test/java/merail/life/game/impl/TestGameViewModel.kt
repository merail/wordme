package merail.life.game.impl

import androidx.lifecycle.SavedStateHandle
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import merail.life.database.api.IDatabaseRepository
import merail.life.domain.KeyCellModel
import merail.life.domain.KeyStateModel
import merail.life.domain.WordIdModel
import merail.life.domain.WordModel
import merail.life.domain.constants.IS_TEST_ENVIRONMENT
import merail.life.game.api.IGameRepository
import merail.life.game.impl.model.Key
import merail.life.game.impl.model.KeyState
import merail.life.game.impl.state.CheckWordKeyState
import merail.life.game.impl.state.DeleteKeyState
import merail.life.game.impl.state.GameResultState
import merail.life.game.impl.state.WordCheckState
import merail.life.game.impl.utils.defaultKeyButtons
import merail.life.game.impl.utils.emptyKeyFields
import merail.life.game.impl.utils.toLogicModel
import merail.life.store.api.IStoreRepository
import merail.life.time.api.ITimeRepository
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestGameViewModel {

    private lateinit var viewModel: GameViewModel
    
    private val savedStateHandle = SavedStateHandle().apply {
        set(IS_TEST_ENVIRONMENT, true)
    }

    private val databaseRepository: IDatabaseRepository = mockk()
    private val storeRepository: IStoreRepository = mockk()
    private val timeRepository: ITimeRepository  = mockk()
    private val gameRepository: IGameRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is game in process`() = runTest(testDispatcher) {
        coEvery { gameRepository.getDayWord() } returns flowOf(WordModel("дубль"))
        val keyCells = mockGameInProcessKeyFormsState()

        viewModel = GameViewModel(
            savedStateHandle = savedStateHandle,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        viewModel.onLoadValuesStart()

        advanceUntilIdle()

        assertEquals(keyCells, viewModel.keyForms.value.toLogicModel())
        assertEquals(Pair(1, 0), viewModel.currentIndex)
        assertEquals(GameResultState.Process, viewModel.gameResultState.value)
    }

    @Test
    fun `initial state is defeat`() = runTest(testDispatcher) {
        coEvery { gameRepository.getDayWord() } returns flowOf(WordModel("дубль"))
        val keyCells = mockDefeatKeyFormsState()

        viewModel = GameViewModel(
            savedStateHandle = savedStateHandle,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        viewModel.onLoadValuesStart()

        advanceUntilIdle()

        assertEquals(keyCells, viewModel.keyForms.value.toLogicModel())
        assertEquals(Pair(6, 0), viewModel.currentIndex)
        assertEquals(GameResultState.Defeat, viewModel.gameResultState.value)
    }

    @Test
    fun `initial state is victory`() = runTest(testDispatcher) {
        coEvery { gameRepository.getDayWord() } returns flowOf(WordModel("дубль"))
        val keyCells = mockVictoryKeyFormsState()

        viewModel = GameViewModel(
            savedStateHandle = savedStateHandle,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        viewModel.onLoadValuesStart()

        advanceUntilIdle()

        assertEquals(keyCells, viewModel.keyForms.value.toLogicModel())
        assertEquals(Pair(1, 0), viewModel.currentIndex)
        assertEquals(GameResultState.Victory, viewModel.gameResultState.value)
    }

    @Test
    fun `startNextDayTimer progresses to next day and updates state`() = runTest {
        coEvery { timeRepository.getTimeUntilNextDay() } returnsMany listOf(
            flowOf(Pair("00:00:01", false)),
            flowOf(Pair("00:00:00", true)),
            flowOf(Pair("23:59:59", false)),
        )
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(1)
        coEvery { databaseRepository.getDayWordId(2) } returns WordIdModel(42)
        coEvery { databaseRepository.getDayWord(42) } returns WordModel("аббат")
        coEvery { storeRepository.removeKeyForms() } just Runs
        coEvery { storeRepository.saveDaysSinceStartCount(any()) } just Runs

        viewModel = GameViewModel(
            savedStateHandle = savedStateHandle,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        timeRepository.getTimeUntilNextDay().collect { (time, isNextDay) ->
            viewModel.onSecondCount(
                time = time,
                isNextDay = isNextDay,
            )
        }

        advanceTimeBy(1000L)

        assertFalse(viewModel.isNextDay.value)

        timeRepository.getTimeUntilNextDay().collect { (time, isNextDay) ->
            viewModel.onSecondCount(
                time = time,
                isNextDay = isNextDay,
            )
        }

        advanceTimeBy(1000L)

        assertEquals(WordModel("аббат"), viewModel.dayWord)
        assertEquals(viewModel.keyForms.value.toLogicModel(), emptyKeyFields.toLogicModel())
        assertEquals(viewModel.keyButtons.value.toLogicModel(), defaultKeyButtons.toLogicModel())
        assertTrue(viewModel.isNextDay.value)
        assertEquals(CheckWordKeyState.Disabled, viewModel.checkWordKeyState.value)
        assertEquals(WordCheckState.None, viewModel.wordCheckState.value)
        assertEquals(GameResultState.Process, viewModel.gameResultState.value)
        assertEquals(Pair(0, 0), viewModel.currentIndex)
        assertFalse(viewModel.isResultBoardVisible.value)

        timeRepository.getTimeUntilNextDay().collect { (time, isNextDay) ->
            viewModel.onSecondCount(
                time = time,
                isNextDay = isNextDay,
            )
        }

        advanceTimeBy(1000L)

        coVerify { storeRepository.removeKeyForms() }
        coVerify { databaseRepository.getDayWordId(2) }
        coVerify { databaseRepository.getDayWord(42) }
        assertFalse(viewModel.isNextDay.value)
    }

    @Test
    fun `disableControlKeys disables keys correctly`() = runTest(testDispatcher) {
        viewModel = GameViewModel(
            savedStateHandle = savedStateHandle,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        viewModel.disableControlKeys()

        assertEquals(DeleteKeyState.Disabled, viewModel.deleteKeyState.value)
        assertEquals(CheckWordKeyState.Disabled, viewModel.checkWordKeyState.value)
    }

    @Test
    fun `handleKeyClick adds and removes keys correctly`() = runTest(testDispatcher) {
        viewModel = GameViewModel(
            savedStateHandle = savedStateHandle,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        viewModel.handleKeyClick(Key.А)
        viewModel.handleKeyClick(Key.Б)
        viewModel.handleKeyClick(Key.DEL)

        assertEquals(1, viewModel.currentIndex.second)
    }

    @Test
    fun `checkWord disables check when word is invalid`() = runTest(testDispatcher) {
        coEvery { databaseRepository.isWordExist("ааааа") } returns false

        viewModel = GameViewModel(
            savedStateHandle = savedStateHandle,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        repeat(5) {
            viewModel.handleKeyClick(Key.А)
        }

        viewModel.handleKeyClick(Key.OK)

        advanceUntilIdle()

        assertEquals(CheckWordKeyState.Disabled, viewModel.checkWordKeyState.value)
        assertEquals(WordCheckState.NonExistentWord(0), viewModel.wordCheckState.value)
    }

    @Test
    fun `checkWord sets ExistingWord state and game is not over`() = runTest(testDispatcher) {
        coEvery { gameRepository.getDayWord() } returns flowOf(WordModel("дубль"))
        coEvery { databaseRepository.isWordExist("ааааа") } returns true
        coEvery { gameRepository.getKeyForms() } returns flowOf(emptyList())
        coEvery { storeRepository.saveKeyForms(any()) } just Runs

        viewModel = GameViewModel(
            savedStateHandle = savedStateHandle,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        viewModel.onLoadValuesStart()

        advanceUntilIdle()

        repeat(5) {
            viewModel.handleKeyClick(Key.А)
        }

        viewModel.handleKeyClick(Key.OK)

        advanceUntilIdle()

        assertEquals(WordCheckState.ExistingWord(0), viewModel.wordCheckState.value)
    }

    @Test
    fun `checkWord sets ExistingWord state and game is over`() = runTest(testDispatcher) {
        coEvery { gameRepository.getDayWord() } returns flowOf(WordModel("дубль"))
        coEvery { databaseRepository.isWordExist("баран") } returns true
        coEvery { gameRepository.getKeyForms() } returns flowOf(emptyList())
        coEvery { storeRepository.saveKeyForms(any()) } just Runs
        coEvery { storeRepository.updateStatsOnDefeat() } just Runs

        viewModel = GameViewModel(
            savedStateHandle = savedStateHandle,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        viewModel.onLoadValuesStart()

        advanceUntilIdle()

        repeat(6) {
            "БАРАН".forEach {
                viewModel.handleKeyClick(Key.valueOf(it.toString()))
            }

            viewModel.handleKeyClick(Key.OK)

            advanceUntilIdle()
        }

        viewModel.handleKeyClick(Key.OK)

        advanceUntilIdle()

        assertEquals(WordCheckState.ExistingWord(5), viewModel.wordCheckState.value)
        assertEquals(GameResultState.Defeat, viewModel.gameResultState.value)
        assertEquals(viewModel.keyForms.value.toLogicModel()[5], oneCorrectKeyCells)
    }

    @Test
    fun `checkWord sets Victory state correctly`() = runTest(testDispatcher) {
        coEvery { gameRepository.getDayWord() } returns flowOf(WordModel("дубль"))
        coEvery { databaseRepository.isWordExist("дубль") } returns true
        coEvery { gameRepository.getKeyForms() } returns flowOf(emptyList())
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(1)
        coEvery { storeRepository.saveKeyForms(any()) } just Runs
        coEvery { storeRepository.saveLastVictoryDay(any()) } just Runs
        coEvery { storeRepository.updateStatsOnVictory(any()) } just Runs

        viewModel = GameViewModel(
            savedStateHandle = savedStateHandle,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        viewModel.onLoadValuesStart()

        advanceUntilIdle()

        "ДУБЛЬ".forEach {
            viewModel.handleKeyClick(Key.valueOf(it.toString()))
        }

        viewModel.handleKeyClick(Key.OK)

        advanceUntilIdle()

        assertEquals(GameResultState.Victory, viewModel.gameResultState.value)
        assertEquals(WordCheckState.CorrectWord(0), viewModel.wordCheckState.value)
        assertEquals(viewModel.keyForms.value.toLogicModel()[0], correctKeyCells)
    }

    @Test
    fun `onFlipAnimationEnd triggers defeat correctly`() = runTest(testDispatcher) {
        coEvery { gameRepository.getDayWord() } returns flowOf(WordModel("дубль"))
        mockDefeatKeyFormsState()

        viewModel = GameViewModel(
            savedStateHandle = savedStateHandle,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        viewModel.onLoadValuesStart()

        advanceUntilIdle()

        val keyButtons = defaultKeyButtons.mapIndexed { r, row ->
            row.mapIndexed { c, cell ->
                when {
                    r == 2 && c == 8 -> cell.copy(
                        state = KeyState.PRESENT,
                    )
                    r == 1 && c == 3 -> cell.copy(
                        state = KeyState.ABSENT,
                    )
                    r == 1 && c == 5 -> cell.copy(
                        state = KeyState.ABSENT,
                    )
                    r == 0 && c == 5 -> cell.copy(
                        state = KeyState.ABSENT,
                    )
                    else -> cell
                }
            }
        }

        viewModel.onFlipAnimationEnd { victory, rowIndex ->
            assertFalse(victory)
            assertEquals(6, rowIndex)
            assertEquals(keyButtons.toLogicModel(), viewModel.keyButtons.value.toLogicModel())
        }
    }

    @Test
    fun `onFlipAnimationEnd triggers victory correctly`() = runTest(testDispatcher) {
        coEvery { gameRepository.getDayWord() } returns flowOf(WordModel("дубль"))
        mockVictoryKeyFormsState()

        viewModel = GameViewModel(
            savedStateHandle = savedStateHandle,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        viewModel.onLoadValuesStart()

        advanceUntilIdle()

        val keyButtons = defaultKeyButtons.mapIndexed { r, row ->
            row.mapIndexed { c, cell ->
                when {
                    r == 1 && c == 8 -> cell.copy(
                        state = KeyState.CORRECT,
                    )
                    r == 0 && c == 2 -> cell.copy(
                        state = KeyState.CORRECT,
                    )
                    r == 2 && c == 8 -> cell.copy(
                        state = KeyState.CORRECT,
                    )
                    r == 1 && c == 7 -> cell.copy(
                        state = KeyState.CORRECT,
                    )
                    r == 2 && c == 7 -> cell.copy(
                        state = KeyState.CORRECT,
                    )
                    else -> cell
                }
            }
        }

        viewModel.onFlipAnimationEnd { victory, rowIndex ->
            assertTrue(victory)
            assertEquals(1, rowIndex)
            assertEquals(keyButtons.toLogicModel(), viewModel.keyButtons.value.toLogicModel())
        }
    }

    private val allAbsentKeyCells = listOf(
        KeyCellModel(
            value = "В",
            state = KeyStateModel.ABSENT,
        ),
        KeyCellModel(
            value = "И",
            state = KeyStateModel.ABSENT,
        ),
        KeyCellModel(
            value = "Х",
            state = KeyStateModel.ABSENT,
        ),
        KeyCellModel(
            value = "О",
            state = KeyStateModel.ABSENT,
        ),
        KeyCellModel(
            value = "Р",
            state = KeyStateModel.ABSENT,
        ),
    )

    private val oneCorrectKeyCells = listOf(
        KeyCellModel(
            value = "Б",
            state = KeyStateModel.PRESENT,
        ),
        KeyCellModel(
            value = "А",
            state = KeyStateModel.ABSENT,
        ),
        KeyCellModel(
            value = "Р",
            state = KeyStateModel.ABSENT,
        ),
        KeyCellModel(
            value = "А",
            state = KeyStateModel.ABSENT,
        ),
        KeyCellModel(
            value = "Н",
            state = KeyStateModel.ABSENT,
        ),
    )

    private val correctKeyCells = listOf(
        KeyCellModel(
            value = "Д",
            state = KeyStateModel.CORRECT,
        ),
        KeyCellModel(
            value = "У",
            state = KeyStateModel.CORRECT,
        ),
        KeyCellModel(
            value = "Б",
            state = KeyStateModel.CORRECT,
        ),
        KeyCellModel(
            value = "Л",
            state = KeyStateModel.CORRECT,
        ),
        KeyCellModel(
            value = "Ь",
            state = KeyStateModel.CORRECT,
        ),
    )

    private fun mockGameInProcessKeyFormsState():  List<List<KeyCellModel>> {
        val keyCells = buildList<List<KeyCellModel>> {
            add(allAbsentKeyCells)
            repeat(ROWS_COUNT - 1) {
                add(emptyList())
            }
        }

        coEvery { gameRepository.getKeyForms() } returns flowOf(keyCells)

        return keyCells
    }

    private fun mockDefeatKeyFormsState(): List<List<KeyCellModel>> {
        val keyCells = buildList {
            repeat(ROWS_COUNT) {
                add(oneCorrectKeyCells)
            }
        }

        coEvery { gameRepository.getKeyForms() } returns flowOf(keyCells)

        return keyCells
    }

    private fun mockVictoryKeyFormsState(): List<List<KeyCellModel>> {
        val keyCells = buildList<List<KeyCellModel>> {
            add(correctKeyCells)
            repeat(ROWS_COUNT - 1) {
                add(emptyList())
            }
        }

        coEvery { gameRepository.getKeyForms() } returns flowOf(keyCells)

        return keyCells
    }
}
