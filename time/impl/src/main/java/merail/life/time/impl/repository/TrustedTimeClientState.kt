package merail.life.time.impl.repository

import com.google.android.gms.time.TrustedTimeClient

internal sealed interface TrustedTimeClientState {

    data object Pending : TrustedTimeClientState

    data object Unavailable : TrustedTimeClientState

    data class Available(
        val trustedTimeClient: TrustedTimeClient,
    ) : TrustedTimeClientState
}