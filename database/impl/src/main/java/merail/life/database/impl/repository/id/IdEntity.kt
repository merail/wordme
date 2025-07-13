package merail.life.database.impl.repository.id

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import merail.life.domain.WordIdModel

@Entity(
    tableName = "id",
)
internal data class IdEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "wordId") val wordId: Int,
)

internal fun IdEntity.toModel() = WordIdModel(
    value = wordId,
)