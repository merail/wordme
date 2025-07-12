package merail.tools.database.words

import androidx.room.Database
import androidx.room.RoomDatabase

const val WORDS_DATABASE_NAME = "words_database"

const val WORDS_DATABASE_FILE = "words_database.db"

@Database(
    entities = [WordEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class WordsDatabase : RoomDatabase() {
    abstract fun dao(): WordDao
}