package merail.tools.database.randomedWordsIds

import androidx.room.Database
import androidx.room.RoomDatabase
import merail.tools.database.wordsIds.WordIdEntity
import merail.tools.database.wordsIds.WordsIdDao

const val RANDOMED_WORDS_IDS_DATABASE_NAME = "randomed_words_ids_database"

const val RANDOMED_WORDS_IDS_DATABASE_FILE = "randomed_words_ids_database.db"

@Database(
    entities = [WordIdEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class RandomedWordsIdsDatabase : RoomDatabase() {
    abstract fun dao(): WordsIdDao
}