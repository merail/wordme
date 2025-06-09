package merail.life.wordme.data.impl.repository.word

import androidx.room.Dao
import androidx.room.Query

@Dao
internal interface WordDao {
    @Query("SELECT * FROM word where id = :id")
    suspend fun getDayWord(id: Int): WordEntity

    @Query("SELECT * FROM word where word = :word")
    suspend fun isWordExist(word: String): WordEntity?
}