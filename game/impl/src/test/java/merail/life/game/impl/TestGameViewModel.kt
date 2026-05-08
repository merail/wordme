package merail.life.game.impl

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import merail.life.core.log.IWordMeLogger
import merail.life.server.api.IServerRepository
import merail.life.domain.KeyCellModel
import merail.life.domain.KeyStateModel
import merail.life.domain.WordModel
import merail.life.game.api.IGameRepository
import merail.life.game.impl.model.Key
import merail.life.game.impl.model.KeyState
import merail.life.game.impl.useCases.CheckWordExistenceUseCase
import merail.life.game.impl.useCases.GetDayWordUseCase
import merail.life.game.impl.state.CheckWordKeyState
import merail.life.game.impl.state.DeleteKeyState
import merail.life.game.impl.state.GameErrorState
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

    private val serverRepository: IServerRepository = mockk()
    private val storeRepository: IStoreRepository = mockk()
    private val timeRepository: ITimeRepository  = mockk()
    private val gameRepository: IGameRepository = mockk()
    private val logger: IWordMeLogger = mockk(relaxed = true)

    private val getDayWordUseCase = GetDayWordUseCase(serverRepository, logger)
    private val checkWordExistenceUseCase = CheckWordExistenceUseCase(serverRepository, logger)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { gameRepository.getDayWord() } returns flowOf(WordModel("дубль"))
        coEvery { gameRepository.getKeyForms() } returns flowOf(emptyList())
        coEvery { gameRepository.setKeyForms(any()) } just Runs
        coEvery { timeRepository.getTimeUntilNextDay() } returns flowOf(Pair("23:59:59", false))
        coEvery { storeRepository.getLastVictoryDay() } returns flowOf(0)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is game in process`() = runTest(testDispatcher) {
        val keyCells = mockGameInProcessKeyFormsState()

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

        assertEquals(keyCells, viewModel.keyForms.value.toLogicModel())
        assertEquals(Pair(1, 0), viewModel.currentIndex)
        assertEquals(GameResultState.Process, viewModel.gameResultState.value)
    }

    @Test
    fun `initial state is defeat`() = runTest(testDispatcher) {
        val keyCells = mockDefeatKeyFormsState()

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

        assertEquals(keyCells, viewModel.keyForms.value.toLogicModel())
        assertEquals(Pair(6, 0), viewModel.currentIndex)
        assertEquals(GameResultState.Defeat, viewModel.gameResultState.value)
    }

    @Test
    fun `initial state is victory`() = runTest(testDispatcher) {
        val keyCells = mockVictoryKeyFormsState()

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

        assertEquals(keyCells, viewModel.keyForms.value.toLogicModel())
        assertEquals(Pair(1, 0), viewModel.currentIndex)
        assertEquals(GameResultState.Victory, viewModel.gameResultState.value)
    }

    @Test
    fun `startNextDayTimer progresses to next day and updates state`() = runTest(testDispatcher) {
        val timeFlow = MutableSharedFlow<Pair<String, Boolean>>(extraBufferCapacity = 1)
        coEvery { timeRepository.getTimeUntilNextDay() } returns timeFlow
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(1)
        coEvery { serverRepository.getDayWord(2) } returns WordModel("аббат")
        coEvery { storeRepository.removeKeyForms() } just Runs
        coEvery { storeRepository.saveDaysSinceStartCount(any()) } just Runs

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

        timeFlow.tryEmit(Pair("00:00:01", false))
        advanceUntilIdle()

        assertFalse(viewModel.isNextDay.value)

        timeFlow.tryEmit(Pair("00:00:00", true))
        advanceUntilIdle()

        assertEquals(WordModel("аббат"), viewModel.dayWord)
        assertEquals(viewModel.keyForms.value.toLogicModel(), emptyKeyFields.toLogicModel())
        assertEquals(viewModel.keyButtons.value.toLogicModel(), defaultKeyButtons.toLogicModel())
        assertTrue(viewModel.isNextDay.value)
        assertEquals(DeleteKeyState.Disabled, viewModel.deleteKeyState.value)
        assertEquals(CheckWordKeyState.Disabled, viewModel.checkWordKeyState.value)
        assertEquals(WordCheckState.None, viewModel.wordCheckState.value)
        assertEquals(GameResultState.Process, viewModel.gameResultState.value)
        assertEquals(Pair(0, 0), viewModel.currentIndex)
        assertFalse(viewModel.isResultBoardVisible.value)

        timeFlow.tryEmit(Pair("23:59:59", false))
        advanceUntilIdle()

        coVerify { storeRepository.removeKeyForms() }
        coVerify { serverRepository.getDayWord(2) }
        assertFalse(viewModel.isNextDay.value)
    }

    @Test
    fun `getDayWord fails on next day sets DayWordGettingError and resets state`() = runTest(testDispatcher) {
        val timeFlow = MutableSharedFlow<Pair<String, Boolean>>(extraBufferCapacity = 1)
        coEvery { timeRepository.getTimeUntilNextDay() } returns timeFlow
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(1)
        coEvery { serverRepository.getDayWord(2) } throws RuntimeException("Network error")
        coEvery { storeRepository.removeKeyForms() } just Runs
        coEvery { storeRepository.saveDaysSinceStartCount(any()) } just Runs

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

        timeFlow.tryEmit(Pair("00:00:00", true))
        advanceUntilIdle()

        assertEquals(GameErrorState.DayWordGettingError, viewModel.gameErrorState.value)
        assertEquals(emptyKeyFields.toLogicModel(), viewModel.keyForms.value.toLogicModel())
        assertEquals(defaultKeyButtons.toLogicModel(), viewModel.keyButtons.value.toLogicModel())
        assertEquals(DeleteKeyState.Disabled, viewModel.deleteKeyState.value)
        assertEquals(CheckWordKeyState.Disabled, viewModel.checkWordKeyState.value)
        assertEquals(WordCheckState.None, viewModel.wordCheckState.value)
        assertEquals(Pair(0, 0), viewModel.currentIndex)
        assertFalse(viewModel.isResultBoardVisible.value)
    }

    @Test
    fun `disableControlKeys disables keys correctly`() = runTest(testDispatcher) {
        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

        viewModel.disableControlKeys()

        assertEquals(DeleteKeyState.Disabled, viewModel.deleteKeyState.value)
        assertEquals(CheckWordKeyState.Disabled, viewModel.checkWordKeyState.value)
    }

    @Test
    fun `handleKeyClick adds and removes keys correctly`() = runTest(testDispatcher) {
        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

        viewModel.handleKeyClick(Key.А)
        viewModel.handleKeyClick(Key.Б)
        viewModel.handleKeyClick(Key.DEL)

        assertEquals(1, viewModel.currentIndex.second)
    }

    @Test
    fun `checkWord disables check when word is invalid`() = runTest(testDispatcher) {
        coEvery { serverRepository.isWordExist("ааааа") } returns false

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

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
        coEvery { serverRepository.isWordExist("ааааа") } returns true
        coEvery { storeRepository.saveKeyForms(any()) } just Runs

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

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
        coEvery { serverRepository.isWordExist("баран") } returns true
        coEvery { storeRepository.saveKeyForms(any()) } just Runs
        coEvery { storeRepository.updateStatsOnDefeat() } just Runs

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

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
        coEvery { serverRepository.isWordExist("дубль") } returns true
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(1)
        coEvery { storeRepository.saveKeyForms(any()) } just Runs
        coEvery { storeRepository.saveLastVictoryDay(any()) } just Runs
        coEvery { storeRepository.updateStatsOnVictory(any()) } just Runs

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

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
        mockDefeatKeyFormsState()

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

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
        mockVictoryKeyFormsState()

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

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

    @Test
    fun `isWordExist throws sets error visible and resets checkWordKeyState`() = runTest(testDispatcher) {
        coEvery { serverRepository.isWordExist("ааааа") } throws RuntimeException("Network error")

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

        repeat(5) {
            viewModel.handleKeyClick(Key.А)
        }

        viewModel.handleKeyClick(Key.OK)

        advanceUntilIdle()

        assertEquals(GameErrorState.WordExistingCheckError, viewModel.gameErrorState.value)
        assertEquals(CheckWordKeyState.Enabled, viewModel.checkWordKeyState.value)
    }

    @Test
    fun `dismissError sets isErrorVisible to false`() = runTest(testDispatcher) {
        coEvery { serverRepository.isWordExist("ааааа") } throws RuntimeException("Network error")

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

        repeat(5) {
            viewModel.handleKeyClick(Key.А)
        }

        viewModel.handleKeyClick(Key.OK)

        advanceUntilIdle()

        assertEquals(GameErrorState.WordExistingCheckError, viewModel.gameErrorState.value)

        viewModel.dismissError()

        assertEquals(GameErrorState.Hidden, viewModel.gameErrorState.value)
    }

    @Test
    fun `on victory, updateStatsOnVictory is not called before saveLastVictoryDay`() = runTest(testDispatcher) {
        val callOrder = mutableListOf<String>()
        val timeDeferred = CompletableDeferred<Int>()

        coEvery { serverRepository.isWordExist("дубль") } returns true
        coEvery { storeRepository.saveKeyForms(any()) } just Runs
        coEvery { storeRepository.saveLastVictoryDay(any()) } answers { callOrder += "saveLastVictoryDay" }
        coEvery { storeRepository.updateStatsOnVictory(any()) } answers { callOrder += "updateStatsOnVictory" }
        coEvery { timeRepository.getDaysSinceStartCount() } returns flow { emit(timeDeferred.await()) }

        viewModel = GameViewModel(
            getDayWordUseCase = getDayWordUseCase,
            checkWordExistenceUseCase = checkWordExistenceUseCase,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )
        advanceUntilIdle()

        "ДУБЛЬ".forEach { viewModel.handleKeyClick(Key.valueOf(it.toString())) }
        viewModel.handleKeyClick(Key.OK)
        advanceUntilIdle()

        assertFalse(
            "updateStatsOnVictory вызван до сохранения lastVictoryDay",
            callOrder.contains("updateStatsOnVictory"),
        )

        timeDeferred.complete(1)
        advanceUntilIdle()

        assertEquals(listOf("saveLastVictoryDay", "updateStatsOnVictory"), callOrder)
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
