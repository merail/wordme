package merail.life.word.data.impl.repository

import androidx.room.Database
import androidx.room.RoomDatabase

internal const val WORDS_DATABASE_NAME = "words_database"

internal const val WORDS_DATABASE_FILE = "words_database.db"

@Database(
    entities = [WordEntity::class],
    version = 1,
    exportSchema = false,
)
internal abstract class WordsDatabase : RoomDatabase() {
    abstract fun wordsElementDao(): WordDao
}