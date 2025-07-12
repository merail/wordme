package merail.tools.database.wordsIds

import androidx.room.Database
import androidx.room.RoomDatabase

const val WORDS_IDS_DATABASE_NAME = "words_ids_database"

const val WORDS_IDS_DATABASE_FILE = "words_ids_database.db"

@Database(
    entities = [WordIdEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class WordsIdsDatabase : RoomDatabase() {
    abstract fun  dao(): WordsIdDao
}