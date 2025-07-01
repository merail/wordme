package merail.life.game.impl

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import merail.life.domain.KeyCellModel
import merail.life.domain.KeyStateModel
import merail.life.domain.WordModel
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestGameRepository {

    private lateinit var repository: GameRepository

    @Before
    fun setup() {
        repository = GameRepository()
    }

    @Test
    fun `setKeyForms should update keyForms state`() = runTest {
        val input = listOf(
            listOf(
                KeyCellModel("А", KeyStateModel.CORRECT),
                KeyCellModel("Б", KeyStateModel.DEFAULT)
            ),
            listOf(
                KeyCellModel("В", KeyStateModel.PRESENT)
            ),
        )

        repository.setKeyForms(input)

        val result = repository.getKeyForms().first()
        assertEquals(input, result)
    }

    @Test
    fun `setDayWord should update dayWord state`() = runTest {
        val word = WordModel("дубль")

        repository.setDayWord(word)

        val result = repository.getDayWord().first()
        assertEquals(word, result)
    }

    @Test
    fun `getKeyForms should initially return null`() = runTest {
        val result = repository.getKeyForms().first()
        assertEquals(null, result)
    }

    @Test
    fun `getDayWord should initially return null`() = runTest {
        val result = repository.getDayWord().first()
        assertEquals(null, result)
    }
}