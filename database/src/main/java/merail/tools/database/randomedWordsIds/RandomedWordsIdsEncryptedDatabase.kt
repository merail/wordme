package merail.tools.database.randomedWordsIds

import androidx.room.Database
import androidx.room.RoomDatabase
import merail.tools.database.wordsIds.WordIdEntity
import merail.tools.database.wordsIds.WordsIdDao

const val RANDOMED_WORDS_IDS_ENCRYPTED_DATABASE_NAME = "encrypted_randomed_words_ids_database"

const val RANDOMED_WORDS_IDS_ENCRYPTED_DATABASE_FILE = "encrypted_randomed_words_ids_database.db"

const val RANDOMED_WORDS_IDS_ENCRYPTED_DATABASE_PASSPHRASE = "wL3n8ZqKd1XpRv6A"

@Database(
    entities = [WordIdEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class RandomedWordsIdsEncryptedDatabase : RoomDatabase() {
    abstract fun dao(): WordsIdDao
}