package merail.life.wordme

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import merail.life.domain.KeyCellModel
import merail.life.store.api.IStoreRepository
import merail.life.store.api.model.StatsModel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FakeStoreModule {

    @Provides
    @Singleton
    fun provideFakeStoreRepository(): IStoreRepository = object : IStoreRepository {
        override suspend fun saveDaysSinceStartCount(count: Int) = Unit

        override fun getDaysSinceStartCount(): Flow<Int> = flowOf(0)

        override suspend fun saveLastVictoryDay(day: Int) = Unit

        override fun getLastVictoryDay(): Flow<Int> = flowOf(0)

        override suspend fun resetVictoriesRowCount() = Unit

        override suspend fun updateStatsOnVictory(attemptsCount: Int) = Unit

        override suspend fun updateStatsOnDefeat() = Unit

        override fun getStats(): Flow<StatsModel> = flowOf(
            StatsModel(
                gamesCount = 0,
                victoriesCount = 0,
                attemptsCount = 0,
                victoriesRowCount = 0,
                victoriesRowMaxCount = 0,
            ),
        )

        override suspend fun saveKeyForms(keyCellModels: List<List<KeyCellModel>>) = Unit

        override fun loadKeyForms(): Flow<List<List<KeyCellModel>>> = flowOf(emptyList())

        override suspend fun removeKeyForms() = Unit
    }
}
