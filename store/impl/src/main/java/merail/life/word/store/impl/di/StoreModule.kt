package merail.life.word.store.impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import merail.life.word.store.api.IStoreRepository
import merail.life.word.store.impl.KeyCells
import merail.life.word.store.impl.KeyCellsSerializer
import merail.life.word.store.impl.repository.DATA_STORE_FILE
import merail.life.word.store.impl.repository.StoreRepository
import merail.life.word.store.impl.repository.preferencesDataStore
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
        fun providePreferencesDataStore(
            @ApplicationContext context: Context,
        ): DataStore<Preferences> = context.preferencesDataStore

        @Provides
        @Singleton
        fun provideProtoDataStore(
            @ApplicationContext context: Context,
            keyCellsSerializer: KeyCellsSerializer,
        ): DataStore<KeyCells> = DataStoreFactory.create(
            serializer = keyCellsSerializer,
        ) {
            context.dataStoreFile(DATA_STORE_FILE)
        }
    }
}