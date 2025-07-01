package merail.life.database.impl.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import merail.life.database.api.IDatabaseRepository
import merail.life.database.impl.repository.DatabaseRepository
import merail.life.database.impl.repository.guessedWordId.GuessedWordsIdsDatabaseProvider
import merail.life.database.impl.repository.word.WORDS_DATABASE_FILE
import merail.life.database.impl.repository.word.WORDS_DATABASE_NAME
import merail.life.database.impl.repository.word.WordsDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DatabaseModule {

    @Singleton
    @Binds
    fun bindDatabaseRepository(
        databaseRepository: DatabaseRepository,
    ): IDatabaseRepository

    companion object {
        @Provides
        @Singleton
        fun provideWordsDatabase(
            @ApplicationContext context: Context,
        ): WordsDatabase = Room.databaseBuilder(
            context = context,
            klass = WordsDatabase::class.java,
            name = WORDS_DATABASE_NAME,
        ).createFromAsset(
            databaseFilePath = WORDS_DATABASE_FILE,
        ).build()

        @Provides
        @Singleton
        fun provideGuessedWordsIdsDatabaseProvider(
            @ApplicationContext context: Context,
        ): GuessedWordsIdsDatabaseProvider = GuessedWordsIdsDatabaseProvider(
            context = context,
        )
    }
}