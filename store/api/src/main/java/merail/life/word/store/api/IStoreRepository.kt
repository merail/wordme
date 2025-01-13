package merail.life.word.store.api

import kotlinx.coroutines.flow.Flow
import merail.life.word.store.api.model.KeyCellModel

interface IStoreRepository {
    suspend fun saveCells(
        keyCellModels: List<List<KeyCellModel>>,
    )

    fun getCells(): Flow<List<List<KeyCellModel>>>
}
