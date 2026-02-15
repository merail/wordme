package merail.life.server.impl

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import merail.life.server.impl.repository.ServerRepository
import merail.life.server.impl.repository.ServerApi
import org.junit.Before
import org.junit.Test

class ServerRepositoryTest {

    private lateinit var serverApi: ServerApi
    private lateinit var repository: ServerRepository

    @Before
    fun setUp() {
        serverApi = mockk()
        repository = ServerRepository(serverApi)
    }

    @Test
    fun `getDayWord returns word from server`() = runTest {
        coEvery { serverApi.getDayWord(1) } returns "аббат"

        val result = repository.getDayWord(1)

        assertEquals("аббат", result.value)
        coVerify { serverApi.getDayWord(1) }
    }

    @Test
    fun `isWordExist returns true when word exists`() = runTest {
        coEvery { serverApi.isWordExist("аббат") } returns true

        val result = repository.isWordExist("аббат")

        assertEquals(true, result)
        coVerify { serverApi.isWordExist("аббат") }
    }

    @Test
    fun `isWordExist returns false when word does not exist`() = runTest {
        coEvery { serverApi.isWordExist("кринж") } returns false

        val result = repository.isWordExist("кринж")

        assertEquals(false, result)
        coVerify { serverApi.isWordExist("кринж") }
    }

    @Test(expected = RuntimeException::class)
    fun `getDayWord propagates exception from server`() = runTest {
        coEvery { serverApi.getDayWord(1) } throws RuntimeException("Network error")

        repository.getDayWord(1)
    }

    @Test(expected = RuntimeException::class)
    fun `isWordExist propagates exception from server`() = runTest {
        coEvery { serverApi.isWordExist("аббат") } throws RuntimeException("Network error")

        repository.isWordExist("аббат")
    }
}
