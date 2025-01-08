package merail.life.word.data.impl.repository

import androidx.room.Dao
import androidx.room.Query

@Dao
internal interface WordDao {

    @Query("SELECT * FROM word where id = :id")
    suspend fun getCurrentWord(id: Int): WordEntity

    @Query("SELECT * FROM word where word = :word")
    suspend fun isWordExist(word: String): WordEntity?
}