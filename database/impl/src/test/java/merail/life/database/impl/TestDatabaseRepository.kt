package merail.life.database.impl

import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import merail.life.database.api.IDatabaseRepository
import merail.life.database.impl.repository.DatabaseRepository
import merail.life.database.impl.repository.id.IdEntity
import merail.life.database.impl.repository.id.IdDao
import merail.life.database.impl.repository.id.IdsDatabase
import merail.life.database.impl.repository.id.IdsDatabaseProvider
import merail.life.database.impl.repository.word.WordDao
import merail.life.database.impl.repository.word.WordEntity
import merail.life.database.impl.repository.word.WordsDatabase
import merail.life.domain.WordIdModel
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DatabaseRepositoryTest {

    private lateinit var wordsDatabase: WordsDatabase
    private lateinit var idsDatabaseProvider: IdsDatabaseProvider

    private lateinit var wordDao: WordDao
    private lateinit var idDao: IdDao

    private lateinit var idsDatabase: IdsDatabase
    private lateinit var repository: IDatabaseRepository

    @Before
    fun setUp() {
        wordDao = mockk()
        idDao = mockk()
        wordsDatabase = mockk {
            every { wordDao() } returns wordDao
        }

        idsDatabase = mockk {
            every { idDao() } returns idDao
        }

        idsDatabaseProvider = mockk {
            every { get() } returns idsDatabase
        }

        repository = DatabaseRepository(wordsDatabase, idsDatabaseProvider)
    }

    @Test
    fun `initIdsDatabase calls provider init`() {
        every { idsDatabaseProvider.init("secret") } just Runs

        repository.initIdsDatabase("secret")

        verify { idsDatabaseProvider.init("secret") }
    }

    @Test
    fun `getDayId returns correct model when id modulo totalCount is not 0`() = runTest {
        val id = 5
        val totalCount = 10
        val expectedIndex = id % totalCount
        val entity = IdEntity(
            id = 10,
            wordId = 377,
        )
        val model = WordIdModel(
            value = 377,
        )

        coEvery { idDao.getCount() } returns totalCount
        coEvery { idDao.getDayId(expectedIndex) } returns entity

        val result = repository.getDayWordId(id)

        assertEquals(model.value, result.value)
    }

    @Test
    fun `getDayId applies totalBias when id modulo totalCount is 0`() = runTest {
        val id = 10
        val totalCount = 10
        val totalBias = 1
        val expectedIndex = (id + totalBias) % totalCount
        val entity = IdEntity(
            id = 10,
            wordId = 377,
        )
        val model = WordIdModel(
            value = 377,
        )

        coEvery { idDao.getCount() } returns totalCount
        coEvery { idDao.getDayId(expectedIndex) } returns entity

        val result = repository.getDayWordId(id)

        assertEquals(model.value, result.value)
    }

    @Test(expected = ArithmeticException::class)
    fun `getDayId throws ArithmeticException when totalCount is 0`() = runTest {
        coEvery { idDao.getCount() } returns 0

        repository.getDayWordId(1)
    }

    @Test
    fun `getDayId returns mapped model`() = runTest {
        val idEntity = IdEntity(
            id = 1,
            wordId = 42,
        )
        coEvery { idDao.getDayId(1) } returns idEntity
        coEvery { idDao.getCount() } returns 1373

        val result = repository.getDayWordId(1)

        assertEquals(42, result.value)
    }

    @Test
    fun `getDayWord returns mapped model`() = runTest {
        val entity = WordEntity(
            id = 1,
            word = "дубль",
            isUndesirable = false,
        )
        coEvery { wordDao.getDayWord(1) } returns entity

        val result = repository.getDayWord(1)

        assertEquals("дубль", result.value)
    }

    @Test
    fun `isWordExist returns true if word exists`() = runTest {
        coEvery { wordDao.isWordExist("дубль") } returns WordEntity(
            id = 1,
            word = "дубль",
            isUndesirable = true,
        )

        val exists = repository.isWordExist("дубль")

        assertEquals(true, exists)
    }

    @Test
    fun `isWordExist returns false if word does not exist`() = runTest {
        coEvery { wordDao.isWordExist("кринж") } returns null

        val exists = repository.isWordExist("кринж")

        assertEquals(false, exists)
    }
}