package merail.life.word.data.impl.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import merail.life.word.data.impl.repository.DatabaseRepository
import merail.life.word.data.impl.repository.WORDS_DATABASE_FILE
import merail.life.word.data.impl.repository.WORDS_DATABASE_NAME
import merail.life.word.data.impl.repository.WordsDatabase
import merail.life.word.database.api.IDatabaseRepository
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
    }
}