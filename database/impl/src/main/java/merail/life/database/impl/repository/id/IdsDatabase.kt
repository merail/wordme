package merail.life.database.impl.repository.id

import androidx.room.Database
import androidx.room.RoomDatabase

internal const val IDS_DATABASE_NAME = "ids_database"

internal const val IDS_DATABASE_FILE = "ids_database.db"

@Database(
    entities = [IdEntity::class],
    version = 1,
    exportSchema = false,
)
internal abstract class IdsDatabase : RoomDatabase() {
    abstract fun  idDao(): IdDao
}