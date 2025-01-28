package merail.tools.database

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {
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
            databaseFilePath = "words_database.db",
        )
            /*.fallbackToDestructiveMigration()*/.build().apply {
            openHelper.writableDatabase
        }
    }
}