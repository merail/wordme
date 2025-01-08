package merail.life.word.data.impl.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import merail.life.word.database.api.model.WordModel

@Entity(
    tableName = "word",
)
internal data class WordEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "code") val code: Int,
    @ColumnInfo(name = "parentCode") val parentCode: Int,
    @ColumnInfo(name = "gender") val gender: String,
    @ColumnInfo(name = "animation") val animation: Boolean?,
    @ColumnInfo(name = "usageCount") val usageCount: Int,
    @ColumnInfo(name = "isUndesirable") val isUndesirable: Boolean,
)

internal fun WordEntity.toModel() = WordModel(
    value = word,
)