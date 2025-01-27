package merail.life.word.store.impl.repository

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import merail.life.word.domain.KeyStateModel
import merail.life.word.store.impl.KeyState

internal const val STATS_STORE_NAME = "stats_store"

internal const val KEY_FORMS_STORE_FILE = "key_forms.pb"

internal fun Context.createPreferencesDataStore(
    fileName: String,
) = PreferenceDataStoreFactory.create(
    produceFile = {
        preferencesDataStoreFile(fileName)
    },
)

internal fun <T> Context.createProtoDataStore(
    fileName: String,
    serializer: Serializer<T>,
) = DataStoreFactory.create(
    serializer = serializer,
) {
    dataStoreFile(fileName)
}

internal fun KeyStateModel.toDto() = when (this) {
    KeyStateModel.ABSENT -> KeyState.ABSENT
    KeyStateModel.PRESENT -> KeyState.PRESENT
    KeyStateModel.CORRECT -> KeyState.CORRECT
    KeyStateModel.DEFAULT -> KeyState.DEFAULT
}

internal fun KeyState.toModel() = when (this) {
    KeyState.ABSENT -> KeyStateModel.ABSENT
    KeyState.PRESENT -> KeyStateModel.PRESENT
    KeyState.CORRECT -> KeyStateModel.CORRECT
    else -> KeyStateModel.DEFAULT
}