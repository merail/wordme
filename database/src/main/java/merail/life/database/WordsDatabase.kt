package merail.life.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

const val WORDS_DATABASE_NAME = "words_database"

@Database(
    entities = [WordEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true,
)
abstract class WordsDatabase : RoomDatabase() {
    abstract fun wordsElementDao(): WordDao
}