package merail.life.word.store.impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import merail.life.word.store.api.IStoreRepository
import merail.life.word.store.impl.KeyCells
import merail.life.word.store.impl.repository.KEY_CELLS_STORE_FILE
import merail.life.word.store.impl.repository.KeyCellsSerializer
import merail.life.word.store.impl.repository.STATS_STORE_NAME
import merail.life.word.store.impl.repository.StoreRepository
import merail.life.word.store.impl.repository.createPreferencesDataStore
import merail.life.word.store.impl.repository.createProtoDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface StoreModule {
    @Singleton
    @Binds
    fun bindStoreRepository(
        storeRepository: StoreRepository,
    ): IStoreRepository

    companion object {
        @Provides
        @Singleton
        fun provideStatsDataStore(
            @ApplicationContext context: Context,
        ): DataStore<Preferences> = context.createPreferencesDataStore(
            fileName = STATS_STORE_NAME,
        )

        @Provides
        @Singleton
        fun provideKeyCellsDataStore(
            @ApplicationContext context: Context,
            keyCellsSerializer: KeyCellsSerializer,
        ): DataStore<KeyCells> = context.createProtoDataStore(
            fileName = KEY_CELLS_STORE_FILE,
            serializer = keyCellsSerializer,
        )
    }
}