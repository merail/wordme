package merail.life.game.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import merail.life.game.api.IGameRepository
import merail.life.game.impl.GameRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface GameModule {

    @Singleton
    @Binds
    fun bindGameRepository(
        gameRepository: GameRepository,
    ): IGameRepository
}