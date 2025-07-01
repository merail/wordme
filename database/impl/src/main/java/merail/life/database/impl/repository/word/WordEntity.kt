package merail.life.database.impl.repository.word

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import merail.life.domain.WordModel

@Entity(
    tableName = "word",
)
internal data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "isUndesirable") val isUndesirable: Boolean,
)

internal fun WordEntity.toModel() = WordModel(
    value = word,
)