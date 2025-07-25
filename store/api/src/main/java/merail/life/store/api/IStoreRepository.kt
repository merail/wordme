package merail.life.store.api

import kotlinx.coroutines.flow.Flow
import merail.life.domain.KeyCellModel
import merail.life.store.api.model.StatsModel

interface IStoreRepository {
    suspend fun saveDaysSinceStartCount(count: Int)

    fun getDaysSinceStartCount(): Flow<Int>

    suspend fun saveLastVictoryDay(day: Int)

    fun getLastVictoryDay(): Flow<Int>

    suspend fun resetVictoriesRowCount()

    suspend fun updateStatsOnVictory(attemptsCount: Int)

    suspend fun updateStatsOnDefeat()

    fun getStats(): Flow<StatsModel>

    suspend fun saveKeyForms(keyCellModels: List<List<KeyCellModel>>)

    fun loadKeyForms(): Flow<List<List<KeyCellModel>>>

    suspend fun removeKeyForms()
}
