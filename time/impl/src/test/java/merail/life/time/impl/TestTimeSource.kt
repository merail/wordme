package merail.life.time.impl

import com.google.android.gms.time.TrustedTimeClient
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import merail.life.time.impl.repository.TRUSTED_TIME_CLIENT_TIMEOUT
import merail.life.time.impl.repository.TimeSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private const val TRUSTED_MILLIS = 42L

@OptIn(ExperimentalCoroutinesApi::class)
class TestTimeSource {

    private lateinit var trustedTimeClient: TrustedTimeClient
    private lateinit var timeSource: TimeSource

    @Before
    fun setUp() {
        trustedTimeClient = mockk()
        every { trustedTimeClient.computeCurrentUnixEpochMillis() } returns TRUSTED_MILLIS
        timeSource = TimeSource()
    }

    @Test
    fun `getCurrentUnixEpochMillis returns trusted time when client is set`() = runTest {
        timeSource.setTimeTrustedClient(
            trustedTimeClient = trustedTimeClient,
        )

        assertEquals(
            TRUSTED_MILLIS,
            timeSource.getCurrentUnixEpochMillis().first(),
        )
    }

    @Test
    fun `getCurrentUnixEpochMillis waits for client set later`() = runTest {
        val millis = async {
            timeSource.getCurrentUnixEpochMillis().first()
        }

        advanceTimeBy(
            delayTime = TRUSTED_TIME_CLIENT_TIMEOUT / 2,
        )
        timeSource.setTimeTrustedClient(
            trustedTimeClient = trustedTimeClient,
        )

        assertEquals(
            TRUSTED_MILLIS,
            millis.await(),
        )
    }

    @Test
    fun `getCurrentUnixEpochMillis returns system time without waiting when client is unavailable`() = runTest {
        timeSource.setTimeTrustedClient(
            trustedTimeClient = null,
        )

        assertSystemTime {
            timeSource.getCurrentUnixEpochMillis().first()
        }
        assertEquals(
            0L,
            currentTime,
        )
    }

    @Test
    fun `getCurrentUnixEpochMillis returns system time after timeout and does not wait again`() = runTest {
        assertSystemTime {
            timeSource.getCurrentUnixEpochMillis().first()
        }
        assertEquals(
            TRUSTED_TIME_CLIENT_TIMEOUT.inWholeMilliseconds,
            currentTime,
        )

        assertSystemTime {
            timeSource.getCurrentUnixEpochMillis().first()
        }
        assertEquals(
            TRUSTED_TIME_CLIENT_TIMEOUT.inWholeMilliseconds,
            currentTime,
        )
    }

    @Test
    fun `getCurrentUnixEpochMillis returns trusted time when client is set after timeout`() = runTest {
        timeSource.getCurrentUnixEpochMillis().first()

        timeSource.setTimeTrustedClient(
            trustedTimeClient = trustedTimeClient,
        )

        assertEquals(
            TRUSTED_MILLIS,
            timeSource.getCurrentUnixEpochMillis().first(),
        )
    }

    @Test
    fun `getCurrentUnixEpochMillis returns system time when client has no time`() = runTest {
        every { trustedTimeClient.computeCurrentUnixEpochMillis() } returns null
        timeSource.setTimeTrustedClient(
            trustedTimeClient = trustedTimeClient,
        )

        assertSystemTime {
            timeSource.getCurrentUnixEpochMillis().first()
        }
    }

    private suspend fun assertSystemTime(
        getMillis: suspend () -> Long,
    ) {
        val before = System.currentTimeMillis()
        val millis = getMillis()
        val after = System.currentTimeMillis()

        assertTrue(
            millis in before..after,
        )
    }
}
