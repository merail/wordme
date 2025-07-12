package merail.tools.database.wordsIds

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordsIdDao {
    @Query("SELECT * FROM wordId")
    suspend fun getAll(): List<WordIdEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordIdEntity>)
}