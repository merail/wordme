package merail.tools.database.wordsIds

import androidx.room.Database
import androidx.room.RoomDatabase

const val WORDS_IDS_ENCRYPTED_DATABASE_NAME = "encrypted_words_ids_database"

const val WORDS_IDS_ENCRYPTED_DATABASE_FILE = "encrypted_words_ids_database.db"

@Database(
    entities = [WordIdEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class WordsIdsEncryptedDatabase : RoomDatabase() {
    abstract fun  dao(): WordsIdDao
}