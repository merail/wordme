package merail.life.time.api

import kotlinx.coroutines.flow.Flow

interface ITimeRepository {

    fun getDaysSinceStartCount(): Flow<Int>

    suspend fun getTimeUntilNextDay(
        reduceTimeFlag: Boolean = BuildConfig.REDUCE_TIME_UNTIL_NEXT_DAY,
    ): Flow<Pair<String, Boolean>>
}