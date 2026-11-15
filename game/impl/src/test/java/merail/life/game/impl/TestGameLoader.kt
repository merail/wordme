package merail.life.game.impl

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import merail.life.config.api.IConfigRepository
import merail.life.core.log.IWordMeLogger
import merail.life.domain.KeyCellModel
import merail.life.domain.KeyStateModel
import merail.life.domain.WordModel
import merail.life.game.api.IGameRepository
import merail.life.game.impl.loader.GameLoader
import merail.life.game.impl.useCases.GetDayWordUseCase
import merail.life.server.api.IServerRepository
import merail.life.store.api.IStoreRepository
import merail.life.time.api.ITimeRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TestGameLoader {

    private val configRepository: IConfigRepository = mockk()
    private val serverRepository: IServerRepository = mockk()
    private val storeRepository: IStoreRepository = mockk()
    private val timeRepository: ITimeRepository = mockk()
    private val gameRepository: IGameRepository = mockk()
    private val logger: IWordMeLogger = mockk(relaxed = true)

    private lateinit var gameLoader: GameLoader

    private val savedKeyForms = listOf(
        listOf(
            KeyCellModel(
                value = "Д",
                state = KeyStateModel.CORRECT,
            ),
        ),
    )

    @Before
    fun setUp() {
        coEvery { configRepository.authAnonymously() } just Runs

        coEvery { serverRepository.getDayWord(any()) } returns WordModel("дубль")

        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(1)

        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(0)
        coEvery { storeRepository.saveDaysSinceStartCount(any()) } just Runs
        coEvery { storeRepository.loadKeyForms() } returns flowOf(savedKeyForms)
        coEvery { storeRepository.removeKeyForms() } just Runs
        coEvery { storeRepository.getLastVictoryDay() } returns flowOf(0)
        coEvery { storeRepository.resetVictoriesRowCount() } just Runs

        coEvery { gameRepository.setDayWord(any()) } just Runs
        coEvery { gameRepository.setKeyForms(any()) } just Runs

        gameLoader = GameLoader(
            configRepository = configRepository,
            getDayWordUseCase = GetDayWordUseCase(
                serverRepository = serverRepository,
                logger = logger,
            ),
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )
    }

    @Test
    fun `loadInitialData on the stored day restores saved key forms`() = runTest {
        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(1)

        gameLoader.loadInitialData()

        coVerify { configRepository.authAnonymously() }
        coVerify { serverRepository.getDayWord(2) }
        coVerify { gameRepository.setDayWord(WordModel("дубль")) }
        coVerify { gameRepository.setKeyForms(savedKeyForms) }
        coVerify(exactly = 0) { storeRepository.removeKeyForms() }
        coVerify(exactly = 0) { storeRepository.saveDaysSinceStartCount(any()) }
    }

    @Test
    fun `loadInitialData on a new day saves it and clears key forms`() = runTest {
        gameLoader.loadInitialData()

        coVerify { serverRepository.getDayWord(2) }
        coVerify { storeRepository.removeKeyForms() }
        coVerify { storeRepository.saveDaysSinceStartCount(1) }
        coVerify { gameRepository.setDayWord(WordModel("дубль")) }
        coVerify { gameRepository.setKeyForms(emptyList()) }
        coVerify(exactly = 0) { storeRepository.loadKeyForms() }
    }

    @Test
    fun `loadInitialData when more than 1 day since last victory resets victories row count`() = runTest {
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(3)
        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(3)
        coEvery { storeRepository.getLastVictoryDay() } returns flowOf(1)

        gameLoader.loadInitialData()

        coVerify { storeRepository.resetVictoriesRowCount() }
    }

    @Test
    fun `loadInitialData when 1 day since last victory keeps victories row count`() = runTest {
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(2)
        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(2)
        coEvery { storeRepository.getLastVictoryDay() } returns flowOf(1)

        gameLoader.loadInitialData()

        coVerify(exactly = 0) { storeRepository.resetVictoriesRowCount() }
    }

    @Test
    fun `loadInitialData fails when auth fails`() = runTest {
        coEvery { configRepository.authAnonymously() } throws RuntimeException()

        val result = runCatching {
            gameLoader.loadInitialData()
        }

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { serverRepository.getDayWord(any()) }
        coVerify(exactly = 0) { gameRepository.setDayWord(any()) }
    }

    @Test
    fun `loadInitialData saves nothing when day word fails to load`() = runTest {
        coEvery { serverRepository.getDayWord(any()) } throws RuntimeException()

        val result = runCatching {
            gameLoader.loadInitialData()
        }

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { storeRepository.removeKeyForms() }
        coVerify(exactly = 0) { storeRepository.saveDaysSinceStartCount(any()) }
        coVerify(exactly = 0) { storeRepository.resetVictoriesRowCount() }
        coVerify(exactly = 0) { gameRepository.setDayWord(any()) }
    }

    @Test
    fun `loadNewDay on a newer day saves it and returns its word`() = runTest {
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(2)
        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(1)
        coEvery { serverRepository.getDayWord(3) } returns WordModel("аббат")

        val newDayWord = gameLoader.loadNewDay()

        assertEquals(WordModel("аббат"), newDayWord)
        coVerify { storeRepository.removeKeyForms() }
        coVerify { storeRepository.saveDaysSinceStartCount(2) }
        coVerify { gameRepository.setDayWord(WordModel("аббат")) }
        coVerify { gameRepository.setKeyForms(emptyList()) }
        coVerify(exactly = 0) { configRepository.authAnonymously() }
    }

    @Test
    fun `loadNewDay when more than 1 day since last victory resets victories row count`() = runTest {
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(3)
        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(2)
        coEvery { storeRepository.getLastVictoryDay() } returns flowOf(1)

        gameLoader.loadNewDay()

        coVerify { storeRepository.resetVictoriesRowCount() }
    }

    @Test
    fun `loadNewDay on the stored day returns null and loads nothing`() = runTest {
        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(1)

        val newDayWord = gameLoader.loadNewDay()

        assertNull(newDayWord)
        coVerify(exactly = 0) { serverRepository.getDayWord(any()) }
        coVerify(exactly = 0) { storeRepository.removeKeyForms() }
        coVerify(exactly = 0) { storeRepository.saveDaysSinceStartCount(any()) }
    }

    @Test
    fun `loadNewDay when stored day is ahead returns null and loads nothing`() = runTest {
        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(2)

        val newDayWord = gameLoader.loadNewDay()

        assertNull(newDayWord)
        coVerify(exactly = 0) { serverRepository.getDayWord(any()) }
        coVerify(exactly = 0) { storeRepository.saveDaysSinceStartCount(any()) }
    }

    @Test
    fun `loadNewDay saves nothing when day word fails to load`() = runTest {
        coEvery { serverRepository.getDayWord(any()) } throws RuntimeException()

        val result = runCatching {
            gameLoader.loadNewDay()
        }

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { storeRepository.removeKeyForms() }
        coVerify(exactly = 0) { storeRepository.saveDaysSinceStartCount(any()) }
        coVerify(exactly = 0) { storeRepository.resetVictoriesRowCount() }
        coVerify(exactly = 0) { gameRepository.setDayWord(any()) }
    }
}
