package merail.life.time.api

import kotlinx.coroutines.flow.Flow

interface ITimeRepository {

    companion object {
        var countdownStartRealTime = System.currentTimeMillis()
        var debugDaysSinceStartCount = 0
    }

    fun setCurrentUnixEpochMillis(currentUnixEpochMillis: Long?)

    fun getDaysSinceStartCount(): Flow<Int>

    suspend fun getTimeUntilNextDay(): Pair<String, Boolean>
}