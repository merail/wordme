package merail.life.database.impl.repository.guessedWordId

import androidx.room.Database
import androidx.room.RoomDatabase

internal const val GUESSED_WORDS_IDS_DATABASE_NAME = "guessed_words_ids_database"

internal const val GUESSED_WORDS_IDS_DATABASE_FILE = "guessed_words_ids_database.db"

@Database(
    entities = [GuessedWordIdEntity::class],
    version = 1,
    exportSchema = false,
)
internal abstract class GuessedWordsIdsDatabase : RoomDatabase() {
    abstract fun  guessedWordsIdDao(): GuessedWordsIdDao
}