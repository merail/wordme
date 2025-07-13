package merail.life.database.impl.repository.id

import android.content.Context
import androidx.room.Room
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Inject

internal class IdsDatabaseProvider @Inject constructor(
    private val context: Context,
) {
    private var db: IdsDatabase? = null

    fun init(password: String) {
        val passphrase = SQLiteDatabase.getBytes(password.toCharArray())
        val factory = SupportFactory(passphrase)

        db = Room.databaseBuilder(
            context = context,
            klass = IdsDatabase::class.java,
            name = IDS_DATABASE_NAME,
        ).openHelperFactory(factory).createFromAsset(
            databaseFilePath = IDS_DATABASE_FILE,
        ).build()
    }

    fun get() = checkNotNull(db)
}