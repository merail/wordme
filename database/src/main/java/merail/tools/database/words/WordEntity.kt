package merail.tools.database.words

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "word",
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "isUndesirable") val isUndesirable: Boolean,
)