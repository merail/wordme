package merail.life.time.api

import com.google.android.gms.time.TrustedTimeClient
import kotlinx.coroutines.flow.Flow

interface ITimeRepository {

    companion object {
        var countdownStartRealTime = System.currentTimeMillis()
        var debugDaysSinceStartCount = 0
    }

    fun setTimeTrustedClient(trustedTimeClient: TrustedTimeClient)

    fun getDaysSinceStartCount(): Flow<Int>

    suspend fun getTimeUntilNextDay(): Pair<String, Boolean>
}