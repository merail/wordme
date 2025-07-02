package merail.life.database.impl.repository.guessedWordId

import androidx.room.Dao
import androidx.room.Query

@Dao
internal interface GuessedWordsIdDao {
    @Query("SELECT * FROM guessedWordId where id = :id")
    suspend fun getDayWordId(id: Int): GuessedWordIdEntity

    @Query("SELECT COUNT(*) FROM guessedWordId")
    suspend fun getCount(): Int
}