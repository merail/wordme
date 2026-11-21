package merail.life.time.impl.repository

import com.google.android.gms.time.TrustedTimeClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull
import merail.life.time.api.ITimeSource
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

internal val TRUSTED_TIME_CLIENT_TIMEOUT = 3.seconds

internal class TimeSource @Inject constructor() : ITimeSource {

    private val trustedTimeClientState = MutableStateFlow<TrustedTimeClientState>(
        value = TrustedTimeClientState.Pending,
    )

    override fun setTimeTrustedClient(
        trustedTimeClient: TrustedTimeClient?,
    ) {
        trustedTimeClientState.value = if (trustedTimeClient != null) {
            TrustedTimeClientState.Available(
                trustedTimeClient = trustedTimeClient,
            )
        } else {
            TrustedTimeClientState.Unavailable
        }
    }

    override fun getCurrentUnixEpochMillis() = flow {
        emit(
            value = getTrustedTimeClient()?.computeCurrentUnixEpochMillis() ?: System.currentTimeMillis(),
        )
    }

    private suspend fun getTrustedTimeClient(): TrustedTimeClient? {
        val awaitedState = withTimeoutOrNull(
            timeout = TRUSTED_TIME_CLIENT_TIMEOUT,
        ) {
            trustedTimeClientState.first {
                it != TrustedTimeClientState.Pending
            }
        }
        if (awaitedState == null) {
            trustedTimeClientState.compareAndSet(
                expect = TrustedTimeClientState.Pending,
                update = TrustedTimeClientState.Unavailable,
            )
        }
        return (trustedTimeClientState.value as? TrustedTimeClientState.Available)?.trustedTimeClient
    }

}
