package merail.life.word.store.impl.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import merail.life.word.store.api.IStoreRepository
import merail.life.word.store.api.model.KeyCellModel
import merail.life.word.store.api.model.KeyCellStateModel
import merail.life.word.store.impl.KeyCell
import merail.life.word.store.impl.KeyCellKt
import merail.life.word.store.impl.KeyCellState
import merail.life.word.store.impl.KeyCells
import merail.life.word.store.impl.KeyField
import merail.life.word.store.impl.copy
import merail.life.word.store.impl.keyField
import javax.inject.Inject

private const val DATA_STORE_NAME = "DataStore"

internal const val DATA_STORE_FILE = "key_cells.pb"

internal val Context.preferencesDataStore by preferencesDataStore(
    name = DATA_STORE_NAME,
)

internal class StoreRepository @Inject constructor(
    private val preferencesDataStore: DataStore<Preferences>,
    private val protoDataStore: DataStore<KeyCells>,
) : IStoreRepository {
    override suspend fun saveCells(keyCellModels: List<List<KeyCellModel>>) {
        protoDataStore.updateData {
            it.copy {
                keyCells.clear()
                keyCellModels.forEach { keyCellModelsList ->
                    val keyField = keyCellModelsList.map { keyCellModel ->
                        KeyCell.newBuilder().setValue(keyCellModel.value).setState(
                            when (keyCellModel.state) {
                                KeyCellStateModel.ABSENT -> KeyCellState.ABSENT
                                KeyCellStateModel.PRESENT -> KeyCellState.PRESENT
                                KeyCellStateModel.CORRECT -> KeyCellState.CORRECT
                                KeyCellStateModel.DEFAULT -> KeyCellState.DEFAULT
                            },
                        ).build()
                    }
                    keyCells.add(
                        value = KeyField.newBuilder().addAllKeyField(keyField).build(),
                    )
                }
            }
        }
    }

    override fun getCells() = protoDataStore.data.map {
        buildList {
            it.keyCellsList.forEach { keyField ->
                add(
                    element = keyField.keyFieldList.map {
                        KeyCellModel(
                            value = it.value,
                            state = when (it.state) {
                                KeyCellState.ABSENT -> KeyCellStateModel.ABSENT
                                KeyCellState.PRESENT -> KeyCellStateModel.PRESENT
                                KeyCellState.CORRECT -> KeyCellStateModel.CORRECT
                                else -> KeyCellStateModel.DEFAULT
                            },
                        )
                    }
                )
            }
        }
//        it.keyCellsList.
//        it.keyCellsMap.entries.map { (value, state) ->
//            KeyCellModel(
//                value = value,
//                state = when (state) {
//                    KeyCellState.ABSENT -> KeyCellStateModel.ABSENT
//                    KeyCellState.PRESENT -> KeyCellStateModel.PRESENT
//                    KeyCellState.CORRECT -> KeyCellStateModel.CORRECT
//                    else -> KeyCellStateModel.DEFAULT
//                }
//            )
//        }
    }
}