package merail.tools.database.wordsIds

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "wordId",
)
data class WordIdEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "wordId") val wordId: Int,
)