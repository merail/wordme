package merail.life.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import merail.life.core.log.IWordMeLogger
import merail.life.core.log.WordMeLogger

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {
    @Binds
    fun bindLogger(
        wordMeLogger: WordMeLogger,
    ): IWordMeLogger
}
