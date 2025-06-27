package merail.life.time.api

import com.google.android.gms.time.TrustedTimeClient

interface ITimeSource {

    fun setTimeTrustedClient(
        trustedTimeClient: TrustedTimeClient,
    )

    fun getCurrentUnixEpochMillis(): Long
}