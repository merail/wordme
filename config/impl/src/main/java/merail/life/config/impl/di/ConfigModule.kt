package merail.life.config.impl.di

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.firestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import merail.life.config.api.IConfigRepository
import merail.life.config.impl.repository.ConfigRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ConfigModule {
    @Singleton
    @Binds
    fun bindConfigRepository(
        configRepository: ConfigRepository,
    ): IConfigRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseFirestore() = Firebase.firestore.apply {
            val localCacheSettings = MemoryCacheSettings.newBuilder().build()

            Firebase.firestore.firestoreSettings = FirebaseFirestoreSettings
                .Builder()
                .setLocalCacheSettings(localCacheSettings)
                .build()
        }

        @Provides
        @Singleton
        fun provideFirebaseAuth() = Firebase.auth
    }
}