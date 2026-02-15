package merail.life.server.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import merail.life.server.api.IServerRepository
import merail.life.server.impl.repository.ServerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ServerModule {
    @Singleton
    @Binds
    fun bindServerRepository(
        serverRepository: ServerRepository,
    ): IServerRepository
}