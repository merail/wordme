package merail.life.database.impl.repository.guessedWordId

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import merail.life.domain.WordIdModel

@Entity(
    tableName = "guessedWordId",
)
internal data class GuessedWordIdEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "guessedWordId") val guessedWordId: Int,
)

internal fun GuessedWordIdEntity.toModel() = WordIdModel(
    value = guessedWordId,
)