package merail.life.time.impl.repository

import com.google.android.gms.time.TrustedTimeClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import merail.life.time.api.ITimeSource
import javax.inject.Inject

internal class TimeSource @Inject constructor() : ITimeSource {

    private val trustedTimeClient = MutableStateFlow<TrustedTimeClient?>(null)

    override fun setTimeTrustedClient(
        trustedTimeClient: TrustedTimeClient?,
    ) {
        this.trustedTimeClient.value = trustedTimeClient
    }

    override fun getCurrentUnixEpochMillis() = trustedTimeClient.map {
        it?.computeCurrentUnixEpochMillis() ?: System.currentTimeMillis()
    }
}