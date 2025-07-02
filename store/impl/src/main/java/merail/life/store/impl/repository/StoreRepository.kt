package merail.life.store.impl.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import merail.life.domain.KeyCellModel
import merail.life.store.api.IStoreRepository
import merail.life.store.api.model.StatsModel
import merail.life.word.store.impl.KeyCell
import merail.life.word.store.impl.KeyCells
import merail.life.word.store.impl.KeyField
import merail.life.word.store.impl.copy
import javax.inject.Inject

internal class StoreRepository @Inject constructor(
    private val statsDataStore: DataStore<Preferences>,
    private val keyFormsDataStore: DataStore<KeyCells>,
) : IStoreRepository {

    companion object {
        private val DAYS_SINCE_START_COUNT_KEY = intPreferencesKey("days_since_start_count")
        private val LAST_VICTORY_DAY_KEY = intPreferencesKey("last_victory_day")
        private val GAMES_COUNT_KEY = intPreferencesKey("games_count")
        private val VICTORIES_COUNT_KEY = intPreferencesKey("victories_count")
        private val ATTEMPTS_COUNT_KEY = intPreferencesKey("attempts_count_key")
        private val VICTORIES_ROW_COUNT_KEY = intPreferencesKey("victories_row_count")
        private val VICTORIES_ROW_MAX_COUNT_KEY = intPreferencesKey("victories_row_max_count")
    }

    override suspend fun saveDaysSinceStartCount(count: Int) {
        statsDataStore.edit { preferences ->
            preferences[DAYS_SINCE_START_COUNT_KEY] = count
        }
    }

    override fun getDaysSinceStartCount() = statsDataStore.data.map { preferences ->
        preferences[DAYS_SINCE_START_COUNT_KEY] ?: 0
    }

    override suspend fun saveLastVictoryDay(day: Int) {
        statsDataStore.edit { preferences ->
            preferences[LAST_VICTORY_DAY_KEY] = day
        }
    }

    override fun getLastVictoryDay() = statsDataStore.data.map { preferences ->
        preferences[LAST_VICTORY_DAY_KEY] ?: 0
    }

    override suspend fun resetVictoriesRowCount() {
        statsDataStore.edit { preferences ->
            preferences[VICTORIES_ROW_COUNT_KEY] = 0
        }
    }

    override suspend fun updateStatsOnVictory(attemptsCount: Int) {
        statsDataStore.edit { preferences ->
            val previousGamesCountValue = preferences[GAMES_COUNT_KEY]
            preferences[GAMES_COUNT_KEY] = (previousGamesCountValue ?: 0) + 1

            val previousVictoriesCountValue = preferences[VICTORIES_COUNT_KEY]
            preferences[VICTORIES_COUNT_KEY] = (previousVictoriesCountValue ?: 0) + 1

            val previousAttemptsCountValue = preferences[ATTEMPTS_COUNT_KEY]
            preferences[ATTEMPTS_COUNT_KEY] = (previousAttemptsCountValue ?: 0) + attemptsCount

            val previousVictoriesRowValue = preferences[VICTORIES_ROW_COUNT_KEY]
            val currentValue = (previousVictoriesRowValue ?: 0) + 1
            val maxVictoriesRowValue = getVictoriesRowMaxCount().first()
            if (currentValue > maxVictoriesRowValue) {
                preferences[VICTORIES_ROW_MAX_COUNT_KEY] = currentValue
            }
            preferences[VICTORIES_ROW_COUNT_KEY] = currentValue
        }
    }

    override suspend fun updateStatsOnDefeat() {
        statsDataStore.edit { preferences ->
            val previousGamesCountValue = preferences[GAMES_COUNT_KEY]
            preferences[GAMES_COUNT_KEY] = (previousGamesCountValue ?: 0) + 1

            preferences[VICTORIES_ROW_COUNT_KEY] = 0
        }
    }

    override fun getStats() = statsDataStore.data.map { preferences ->
        StatsModel(
            gamesCount = preferences[GAMES_COUNT_KEY] ?: 0,
            victoriesCount = preferences[VICTORIES_COUNT_KEY] ?: 0,
            attemptsCount = preferences[ATTEMPTS_COUNT_KEY] ?: 0,
            victoriesRowCount = preferences[VICTORIES_ROW_COUNT_KEY] ?: 0,
            victoriesRowMaxCount = preferences[VICTORIES_ROW_MAX_COUNT_KEY] ?: 0,
        )
    }

    override suspend fun saveKeyForms(keyCellModels: List<List<KeyCellModel>>) {
        keyFormsDataStore.updateData { keyCellsDto ->
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

    override fun loadKeyForms() = keyFormsDataStore.data.map {
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

    override suspend fun removeKeyForms() {
        keyFormsDataStore.updateData { preferences ->
            preferences
                .toBuilder()
                .clearKeyCells()
                .build()
        }
    }

    private fun getVictoriesRowMaxCount() = statsDataStore.data.map { preferences ->
        preferences[VICTORIES_ROW_MAX_COUNT_KEY] ?: 0
    }
}