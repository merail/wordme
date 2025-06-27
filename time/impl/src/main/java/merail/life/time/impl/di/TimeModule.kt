package merail.life.time.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import merail.life.time.api.ITimeRepository
import merail.life.time.api.ITimeSource
import merail.life.time.impl.repository.TimeRepository
import merail.life.time.impl.repository.TimeSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface TimeModule {
    @Singleton
    @Binds
    fun bindTimeSource(
        timeSource: TimeSource,
    ): ITimeSource

    @Singleton
    @Binds
    fun bindTimeRepository(
        timeRepository: TimeRepository,
    ): ITimeRepository
}