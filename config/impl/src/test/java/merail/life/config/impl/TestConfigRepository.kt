package merail.life.config.impl

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import io.mockk.*
import kotlinx.coroutines.test.runTest
import merail.life.config.impl.repository.ConfigRepository
import merail.life.domain.exceptions.NoInternetConnectionException
import merail.life.domain.exceptions.TestFirebaseException
import org.junit.Before
import org.junit.Test

class TestConfigRepository {

    private lateinit var auth: FirebaseAuth
    private lateinit var repository: ConfigRepository

    @Before
    fun setUp() {
        auth = mockk()
        repository = ConfigRepository(auth)
    }

    @Test
    fun `authAnonymously calls FirebaseAuth`() = runTest {
        val mockResult = mockk<AuthResult>()
        coEvery { auth.signInAnonymously() } returns Tasks.forResult(mockResult)

        repository.authAnonymously()

        coVerify { auth.signInAnonymously() }
    }

    @Test(expected = NoInternetConnectionException::class)
    fun `authAnonymously throws NoInternetConnectionException on Firebase error`() = runTest {
        every {
            auth.signInAnonymously()
        } throws TestFirebaseException()

        repository.authAnonymously()
    }
}
