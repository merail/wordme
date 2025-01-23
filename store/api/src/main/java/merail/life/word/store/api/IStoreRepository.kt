package merail.life.word.store.api

import kotlinx.coroutines.flow.Flow
import merail.life.word.domain.KeyCellModel
import merail.life.word.store.api.model.StatsModel

interface IStoreRepository {
    suspend fun updateStatsOnVictory(attemptsCount: Int)

    suspend fun updateStatsOnOnDefeat()

    fun getStats(): Flow<StatsModel>

    suspend fun saveKeyCells(keyCellModels: List<List<KeyCellModel>>)

    fun loadKeyCells(): Flow<List<List<KeyCellModel>>>
}
