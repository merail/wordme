package merail.life.word.store.impl.repository

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.map
import merail.life.word.domain.KeyCellModel
import merail.life.word.store.api.IStoreRepository
import merail.life.word.store.impl.KeyCell
import merail.life.word.store.impl.KeyCells
import merail.life.word.store.impl.KeyField
import merail.life.word.store.impl.copy
import javax.inject.Inject

internal class StoreRepository @Inject constructor(
    private val keyCellsDataStore: DataStore<KeyCells>,
) : IStoreRepository {
    override suspend fun saveKeyCells(keyCellModels: List<List<KeyCellModel>>) {
        keyCellsDataStore.updateData { keyCellsDto ->
            keyCellsDto.copy {
                keyCells.clear()
                keyCellModels.forEach { keyCellModelsList ->
                    val keyField = keyCellModelsList.map { keyCellModel ->
                        KeyCell.newBuilder()
                            .setValue(keyCellModel.value)
                            .setState(keyCellModel.state.toDto())
                            .build()
                    }
                    keyCells.add(
                        value = KeyField.newBuilder().addAllKeyField(keyField).build(),
                    )
                }
            }
        }
    }

    override fun loadKeyCells() = keyCellsDataStore.data.map {
        buildList {
            it.keyCellsList.forEach { keyField ->
                add(
                    element = keyField.keyFieldList.map {
                        KeyCellModel(
                            value = it.value,
                            state = it.state.toModel(),
                        )
                    }
                )
            }
        }
    }
}