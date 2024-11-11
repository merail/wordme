package merail.life.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "word",
)
data class WordEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "code") val code: Int,
    @ColumnInfo(name = "parentCode") val parentCode: Int,
    @ColumnInfo(name = "gender") val gender: String,
    @ColumnInfo(name = "case") val case: String?,
    @ColumnInfo(name = "animation") val animation: Boolean?,
    @ColumnInfo(name = "usageCount", defaultValue = "0") val usageCount: Int,
)