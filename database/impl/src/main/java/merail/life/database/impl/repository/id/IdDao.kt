package merail.life.database.impl.repository.id

import androidx.room.Dao
import androidx.room.Query

@Dao
internal interface IdDao {
    @Query("SELECT * FROM id where id = :id")
    suspend fun getDayId(id: Int): IdEntity

    @Query("SELECT COUNT(*) FROM id")
    suspend fun getCount(): Int
}