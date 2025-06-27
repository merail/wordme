package merail.life.time.api

import com.google.android.gms.time.TrustedTimeClient
import kotlinx.coroutines.flow.Flow

interface ITimeSource {

    fun setTimeTrustedClient(
        trustedTimeClient: TrustedTimeClient?,
    )

    fun getCurrentUnixEpochMillis(): Flow<Long>
}