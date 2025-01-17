package merail.life.word.store.api

import kotlinx.coroutines.flow.Flow
import merail.life.word.domain.KeyCellModel

interface IStoreRepository {
    suspend fun saveKeyCells(
        keyCellModels: List<List<KeyCellModel>>,
    )

    fun loadKeyCells(): Flow<List<List<KeyCellModel>>>
}
