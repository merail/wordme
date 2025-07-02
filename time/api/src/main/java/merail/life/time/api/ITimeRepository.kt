package merail.life.time.api

import kotlinx.coroutines.flow.Flow

interface ITimeRepository {

    companion object {
        var countdownStartRealTime = System.currentTimeMillis()
        var debugDaysSinceStartCount = 0
    }

    fun getDaysSinceStartCount(): Flow<Int>

    suspend fun getTimeUntilNextDay(
        reduceTimeFlag: Boolean = BuildConfig.REDUCE_TIME_UNTIL_NEXT_DAY,
    ): Pair<String, Boolean>
}