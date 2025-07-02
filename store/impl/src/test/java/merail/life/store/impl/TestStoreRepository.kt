package merail.life.store.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import merail.life.domain.KeyCellModel
import merail.life.domain.KeyStateModel
import merail.life.store.impl.repository.StoreRepository
import merail.life.word.store.impl.KeyCells
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestStoreRepository {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var storeRepository: StoreRepository

    private lateinit var statsDataStore: DataStore<Preferences>
    private lateinit var keyFormsDataStore: DataStore<KeyCells>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        statsDataStore = FakePreferencesDataStore()
        keyFormsDataStore = FakeProtoDataStore()

        storeRepository = StoreRepository(
            statsDataStore = statsDataStore,
            keyFormsDataStore = keyFormsDataStore,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `save and get DaysSinceStartCount`() = runTest {
        storeRepository.saveDaysSinceStartCount(7)
        val result = storeRepository.getDaysSinceStartCount().first()
        assertEquals(7, result)
    }

    @Test
    fun `save and get LastVictoryDay`() = runTest {
        storeRepository.saveLastVictoryDay(14)
        val result = storeRepository.getLastVictoryDay().first()
        assertEquals(14, result)
    }

    @Test
    fun `resetVictoriesRowCount sets value to 0`() = runTest {
        storeRepository.resetVictoriesRowCount()
        val stats = storeRepository.getStats().first()
        assertEquals(0, stats.victoriesRowCount)
    }

    @Test
    fun `updateStatsOnVictory updates stats correctly`() = runTest {
        storeRepository.updateStatsOnVictory(3)
        val stats = storeRepository.getStats().first()
        assertEquals(1, stats.gamesCount)
        assertEquals(1, stats.victoriesCount)
        assertEquals(3, stats.attemptsCount)
        assertEquals(1, stats.victoriesRowCount)
        assertEquals(1, stats.victoriesRowMaxCount)
    }

    @Test
    fun `updateStatsOnDefeat resets streak and adds game`() = runTest {
        storeRepository.updateStatsOnVictory(2)
        storeRepository.updateStatsOnDefeat()
        val stats = storeRepository.getStats().first()
        assertEquals(2, stats.gamesCount)
        assertEquals(1, stats.victoriesCount)
        assertEquals(2, stats.attemptsCount)
        assertEquals(0, stats.victoriesRowCount)
    }

    @Test
    fun `save and load key forms`() = runTest {
        val keyCell = KeyCellModel("А", KeyStateModel.CORRECT)
        val input = listOf(listOf(keyCell))
        storeRepository.saveKeyForms(input)
        val result = storeRepository.loadKeyForms().first()
        assertEquals(1, result.size)
        assertEquals("А", result[0][0].value)
        assertEquals(KeyStateModel.CORRECT, result[0][0].state)
    }

    @Test
    fun `remove key forms clears store`() = runTest {
        storeRepository.saveKeyForms(listOf(listOf(KeyCellModel("Б", KeyStateModel.PRESENT))))
        storeRepository.removeKeyForms()
        val result = storeRepository.loadKeyForms().first()
        assertTrue(result.isEmpty())
    }
}

class FakePreferencesDataStore : DataStore<Preferences> {
    private val backingFlow = MutableStateFlow<Preferences>(preferencesOf())
    override val data: Flow<Preferences> = backingFlow

    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
        val updated = transform(backingFlow.value)
        backingFlow.value = updated
        return updated
    }
}

class FakeProtoDataStore : DataStore<KeyCells> {
    private val stateFlow = MutableStateFlow(KeyCells.getDefaultInstance())
    override val data: Flow<KeyCells> = stateFlow

    override suspend fun updateData(transform: suspend (KeyCells) -> KeyCells): KeyCells {
        val updated = transform(stateFlow.value)
        stateFlow.value = updated
        return updated
    }
}