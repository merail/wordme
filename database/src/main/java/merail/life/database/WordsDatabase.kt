package merail.life.database

import androidx.room.Database
import androidx.room.RoomDatabase

const val WORDS_DATABASE_NAME = "words_database"

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
abstract class WordsDatabase : RoomDatabase() {
    abstract fun wordsElementDao(): WordDao
}