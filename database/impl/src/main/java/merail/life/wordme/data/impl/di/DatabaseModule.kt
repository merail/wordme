package merail.life.wordme.data.impl.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import merail.life.wordme.data.impl.repository.DatabaseRepository
import merail.life.wordme.data.impl.repository.guessedWordId.GUESSED_WORDS_IDS_DATABASE_FILE
import merail.life.wordme.data.impl.repository.guessedWordId.GUESSED_WORDS_IDS_DATABASE_NAME
import merail.life.wordme.data.impl.repository.guessedWordId.GuessedWordsIdsDatabase
import merail.life.wordme.data.impl.repository.word.WORDS_DATABASE_FILE
import merail.life.wordme.data.impl.repository.word.WORDS_DATABASE_NAME
import merail.life.wordme.data.impl.repository.word.WordsDatabase
import merail.life.database.api.IDatabaseRepository
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
            context,
            WordsDatabase::class.java,
            WORDS_DATABASE_NAME,
        ).createFromAsset(
            databaseFilePath = WORDS_DATABASE_FILE,
        ).build().apply {
            openHelper.writableDatabase
        }

        @Provides
        @Singleton
        fun provideGuessedWordsIdsDatabase(
            @ApplicationContext context: Context,
        ): GuessedWordsIdsDatabase = Room.databaseBuilder(
            context,
            GuessedWordsIdsDatabase::class.java,
            GUESSED_WORDS_IDS_DATABASE_NAME,
        ).createFromAsset(
            databaseFilePath = GUESSED_WORDS_IDS_DATABASE_FILE,
        ).build().apply {
            openHelper.writableDatabase
        }
    }
}