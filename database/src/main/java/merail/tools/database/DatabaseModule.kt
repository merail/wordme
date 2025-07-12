package merail.tools.database

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import merail.life.database.BuildConfig
import merail.tools.database.randomedWordsIds.RANDOMED_WORDS_IDS_DATABASE_FILE
import merail.tools.database.randomedWordsIds.RANDOMED_WORDS_IDS_DATABASE_NAME
import merail.tools.database.randomedWordsIds.RANDOMED_WORDS_IDS_ENCRYPTED_DATABASE_NAME
import merail.tools.database.randomedWordsIds.RANDOMED_WORDS_IDS_ENCRYPTED_DATABASE_PASSPHRASE
import merail.tools.database.randomedWordsIds.RandomedWordsIdsDatabase
import merail.tools.database.randomedWordsIds.RandomedWordsIdsEncryptedDatabase
import merail.tools.database.words.WORDS_DATABASE_FILE
import merail.tools.database.words.WORDS_DATABASE_NAME
import merail.tools.database.words.WORDS_ENCRYPTED_DATABASE_NAME
import merail.tools.database.words.WordsDatabase
import merail.tools.database.words.WordsEncryptedDatabase
import merail.tools.database.wordsIds.WORDS_IDS_DATABASE_FILE
import merail.tools.database.wordsIds.WORDS_IDS_DATABASE_NAME
import merail.tools.database.wordsIds.WORDS_IDS_ENCRYPTED_DATABASE_NAME
import merail.tools.database.wordsIds.WordsIdsDatabase
import merail.tools.database.wordsIds.WordsIdsEncryptedDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton
import kotlin.jvm.java
import kotlin.text.toCharArray

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {

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
        ).fallbackToDestructiveMigration().build().apply {
            openHelper.writableDatabase
        }

        @Provides
        @Singleton
        fun provideWordsEncryptedDatabase(
            @ApplicationContext context: Context,
        ): WordsEncryptedDatabase = Room.databaseBuilder(
            context,
            WordsEncryptedDatabase::class.java,
            WORDS_ENCRYPTED_DATABASE_NAME,
        ).openHelperFactory(
            factory = SupportFactory(
                SQLiteDatabase.getBytes(BuildConfig.WORDS_ENCRYPTED_DATABASE_PASSPHRASE.toCharArray()),
            ),
        ).build()

        @Provides
        @Singleton
        fun provideWordsIdsDatabase(
            @ApplicationContext context: Context,
        ): WordsIdsDatabase = Room.databaseBuilder(
            context,
            WordsIdsDatabase::class.java,
            WORDS_IDS_DATABASE_NAME,
        ).createFromAsset(
            databaseFilePath = WORDS_IDS_DATABASE_FILE,
        ).build().apply {
            openHelper.writableDatabase
        }

        @Provides
        @Singleton
        fun provideWordsIdsEncryptedDatabase(
            @ApplicationContext context: Context,
        ): WordsIdsEncryptedDatabase = Room.databaseBuilder(
            context,
            WordsIdsEncryptedDatabase::class.java,
            WORDS_IDS_ENCRYPTED_DATABASE_NAME,
        ).openHelperFactory(
            factory = SupportFactory(
                SQLiteDatabase.getBytes(BuildConfig.WORDS_IDS_ENCRYPTED_DATABASE_PASSPHRASE.toCharArray()),
            ),
        ).build()

        @Provides
        @Singleton
        fun provideRandomedWordsIdsDatabase(
            @ApplicationContext context: Context,
        ): RandomedWordsIdsDatabase = Room.databaseBuilder(
            context,
            RandomedWordsIdsDatabase::class.java,
            RANDOMED_WORDS_IDS_DATABASE_NAME,
        ).createFromAsset(
            databaseFilePath = RANDOMED_WORDS_IDS_DATABASE_FILE,
        ).build().apply {
            openHelper.writableDatabase
        }

        @Provides
        @Singleton
        fun provideRandomedWordsIdsEncryptedDatabase(
            @ApplicationContext context: Context,
        ): RandomedWordsIdsEncryptedDatabase = Room.databaseBuilder(
            context,
            RandomedWordsIdsEncryptedDatabase::class.java,
            RANDOMED_WORDS_IDS_ENCRYPTED_DATABASE_NAME,
        ).openHelperFactory(
            factory = SupportFactory(
                SQLiteDatabase.getBytes(RANDOMED_WORDS_IDS_ENCRYPTED_DATABASE_PASSPHRASE.toCharArray()),
            ),
        ).build()
    }
}