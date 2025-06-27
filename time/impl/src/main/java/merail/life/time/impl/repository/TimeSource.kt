package merail.life.time.impl.repository

import com.google.android.gms.time.TrustedTimeClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import merail.life.time.api.ITimeSource
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

internal class TimeSource : ITimeSource {

    private val trustedTimeClient = MutableStateFlow<TrustedTimeClient?>(null)

    private val currentLocalDateTime: Flow<LocalDateTime?>
        get() = trustedTimeClient.map { nullableTrustedTimeClient ->
            nullableTrustedTimeClient?.let { trustedTimeClient->
                trustedTimeClient.computeCurrentUnixEpochMillis()?.let {
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                }
            }
        }

    override fun setTimeTrustedClient(
        trustedTimeClient: TrustedTimeClient,
    ) {
        this.trustedTimeClient.value = trustedTimeClient
    }

    override fun getCurrentUnixEpochMillis() = trustedTimeClient
        .value
        ?.computeCurrentUnixEpochMillis() ?: System.currentTimeMillis()
}