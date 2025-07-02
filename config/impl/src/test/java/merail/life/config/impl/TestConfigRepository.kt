package merail.life.config.impl

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import merail.life.config.impl.repository.ConfigRepository
import merail.life.config.impl.repository.ConfigRepository.Companion.CONFIG_KEY
import merail.life.config.impl.repository.ConfigRepository.Companion.GAME_COUNTDOWN_START_DATE_KEY
import merail.life.config.impl.repository.ConfigRepository.Companion.IDS_DATABASE_PASSWORD_KEY
import merail.life.config.impl.repository.ConfigRepository.Companion.VALUE_KEY
import merail.life.domain.exceptions.NoInternetConnectionException
import merail.life.domain.exceptions.TestFirebaseException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestConfigRepository {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: ConfigRepository

    @Before
    fun setUp() {
        firestore = mockk()
        auth = mockk()
        repository = ConfigRepository(firestore, auth)
    }

    @Test
    fun `authAnonymously calls FirebaseAuth`() = runTest {
        val mockResult = mockk<AuthResult>()
        coEvery { auth.signInAnonymously() } returns Tasks.forResult(mockResult)

        repository.authAnonymously()

        coVerify { auth.signInAnonymously() }
    }

    @Test(expected = NoInternetConnectionException::class)
    fun `authAnonymously throws NoInternetConnectionException on Firestore error`() = runTest {
        every {
            auth.signInAnonymously()
        } throws TestFirebaseException()

        repository.authAnonymously()
    }

    @Test
    fun `fetchInitialValues loads values from Firestore`() = runTest {
        val password = "secret"
        val date = "2025-07-01"

        val passwordSnapshot = mockk<DocumentSnapshot> {
            every { getString(VALUE_KEY) } returns password
        }

        val dateSnapshot = mockk<DocumentSnapshot> {
            every { getString(VALUE_KEY) } returns date
        }

        every {
            firestore.collection(CONFIG_KEY).document(IDS_DATABASE_PASSWORD_KEY).get()
        } returns Tasks.forResult(passwordSnapshot)

        every {
            firestore.collection(CONFIG_KEY).document(GAME_COUNTDOWN_START_DATE_KEY).get()
        } returns Tasks.forResult(dateSnapshot)

        repository.fetchInitialValues()

        assertEquals(password, repository.getIdsDatabasePassword().first())
        assertEquals(date, repository.getGameCountdownStartDate().first())
    }

    @Test(expected = NoInternetConnectionException::class)
    fun `fetchInitialValues throws NoInternetConnectionException on Firestore error`() = runTest {
        every {
            firestore.collection(CONFIG_KEY).document(any()).get()
        } throws TestFirebaseException()

        repository.fetchInitialValues()
    }

    @Test
    fun `initial values are empty before fetch`() = runTest {
        assertEquals("", repository.getIdsDatabasePassword().first())
        assertEquals("", repository.getGameCountdownStartDate().first())
    }
}