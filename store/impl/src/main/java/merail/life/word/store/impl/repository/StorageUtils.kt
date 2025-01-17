package merail.life.word.store.impl.repository

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import merail.life.word.domain.KeyCellStateModel
import merail.life.word.store.impl.KeyCellState

internal const val KEY_CELLS_STORE_FILE = "key_cells.pb"

internal fun <T> Context.createProtoDataStore(
    fileName: String,
    serializer: Serializer<T>,
) = DataStoreFactory.create(
    serializer = serializer,
) {
    dataStoreFile(fileName)
}

internal fun KeyCellStateModel.toDto() = when (this) {
    KeyCellStateModel.ABSENT -> KeyCellState.ABSENT
    KeyCellStateModel.PRESENT -> KeyCellState.PRESENT
    KeyCellStateModel.CORRECT -> KeyCellState.CORRECT
    KeyCellStateModel.DEFAULT -> KeyCellState.DEFAULT
}

internal fun KeyCellState.toModel() = when (this) {
    KeyCellState.ABSENT -> KeyCellStateModel.ABSENT
    KeyCellState.PRESENT -> KeyCellStateModel.PRESENT
    KeyCellState.CORRECT -> KeyCellStateModel.CORRECT
    else -> KeyCellStateModel.DEFAULT
}