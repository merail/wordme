package merail.life.database.impl.repository.guessedWordId

import android.content.Context
import androidx.room.Room
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Inject

internal class GuessedWordsIdsDatabaseProvider @Inject constructor(
    private val context: Context,
) {
    private var db: GuessedWordsIdsDatabase? = null

    fun init(password: String) {
        val passphrase = SQLiteDatabase.getBytes(password.toCharArray())
        val factory = SupportFactory(passphrase)

        db = Room.databaseBuilder(
            context = context,
            klass = GuessedWordsIdsDatabase::class.java,
            name = GUESSED_WORDS_IDS_DATABASE_NAME,
        ).openHelperFactory(factory).createFromAsset(
            databaseFilePath = GUESSED_WORDS_IDS_DATABASE_FILE,
        ).build()
    }

    fun get() = checkNotNull(db)
}