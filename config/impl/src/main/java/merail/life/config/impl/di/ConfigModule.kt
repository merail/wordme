package merail.life.config.impl.di

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
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
        fun provideFirebaseRemoteConfig() = Firebase.remoteConfig
    }
}