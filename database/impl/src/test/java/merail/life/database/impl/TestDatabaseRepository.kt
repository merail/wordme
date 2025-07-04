package merail.life.database.impl

import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import merail.life.database.api.IDatabaseRepository
import merail.life.database.impl.repository.DatabaseRepository
import merail.life.database.impl.repository.guessedWordId.GuessedWordIdEntity
import merail.life.database.impl.repository.guessedWordId.GuessedWordsIdDao
import merail.life.database.impl.repository.guessedWordId.GuessedWordsIdsDatabase
import merail.life.database.impl.repository.guessedWordId.GuessedWordsIdsDatabaseProvider
import merail.life.database.impl.repository.word.WordDao
import merail.life.database.impl.repository.word.WordEntity
import merail.life.database.impl.repository.word.WordsDatabase
import merail.life.domain.WordIdModel
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DatabaseRepositoryTest {

    private lateinit var wordsDatabase: WordsDatabase
    private lateinit var guessedWordsIdsDatabaseProvider: GuessedWordsIdsDatabaseProvider

    private lateinit var wordDao: WordDao
    private lateinit var guessedWordsIdDao: GuessedWordsIdDao

    private lateinit var guessedWordsIdsDatabase: GuessedWordsIdsDatabase
    private lateinit var repository: IDatabaseRepository

    @Before
    fun setUp() {
        wordDao = mockk()
        guessedWordsIdDao = mockk()
        wordsDatabase = mockk {
            every { wordDao() } returns wordDao
        }

        guessedWordsIdsDatabase = mockk {
            every { guessedWordsIdDao() } returns guessedWordsIdDao
        }

        guessedWordsIdsDatabaseProvider = mockk {
            every { get() } returns guessedWordsIdsDatabase
        }

        repository = DatabaseRepository(wordsDatabase, guessedWordsIdsDatabaseProvider)
    }

    @Test
    fun `initIdsDatabase calls provider init`() {
        every { guessedWordsIdsDatabaseProvider.init("secret") } just Runs

        repository.initIdsDatabase("secret")

        verify { guessedWordsIdsDatabaseProvider.init("secret") }
    }

    @Test
    fun `getDayWordId returns correct model when id modulo totalCount is not 0`() = runTest {
        val id = 5
        val totalCount = 10
        val expectedIndex = id % totalCount
        val entity = GuessedWordIdEntity(
            id = 10,
            guessedWordId = 377,
        )
        val model = WordIdModel(
            value = 377,
        )

        coEvery { guessedWordsIdDao.getCount() } returns totalCount
        coEvery { guessedWordsIdDao.getDayWordId(expectedIndex) } returns entity

        val result = repository.getDayWordId(id)

        assertEquals(model.value, result.value)
    }

    @Test
    fun `getDayWordId applies totalBias when id modulo totalCount is 0`() = runTest {
        val id = 10
        val totalCount = 10
        val totalBias = 1
        val expectedIndex = (id + totalBias) % totalCount
        val entity = GuessedWordIdEntity(
            id = 10,
            guessedWordId = 377,
        )
        val model = WordIdModel(
            value = 377,
        )

        coEvery { guessedWordsIdDao.getCount() } returns totalCount
        coEvery { guessedWordsIdDao.getDayWordId(expectedIndex) } returns entity

        val result = repository.getDayWordId(id)

        assertEquals(model.value, result.value)
    }

    @Test(expected = ArithmeticException::class)
    fun `getDayWordId throws ArithmeticException when totalCount is 0`() = runTest {
        coEvery { guessedWordsIdDao.getCount() } returns 0

        repository.getDayWordId(1)
    }

    @Test
    fun `getDayWordId returns mapped model`() = runTest {
        val wordIdEntity = GuessedWordIdEntity(
            id = 1,
            guessedWordId = 42,
        )
        coEvery { guessedWordsIdDao.getDayWordId(1) } returns wordIdEntity
        coEvery { guessedWordsIdDao.getCount() } returns 1373

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