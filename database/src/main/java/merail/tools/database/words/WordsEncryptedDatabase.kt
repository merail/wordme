package merail.tools.database.words

import androidx.room.Database
import androidx.room.RoomDatabase

const val WORDS_ENCRYPTED_DATABASE_NAME = "words_encrypted_database"

const val WORDS_ENCRYPTED_DATABASE_FILE = "words_encrypted_database.db"

@Database(
    entities = [WordEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class WordsEncryptedDatabase : RoomDatabase() {
    abstract fun dao(): WordDao
}