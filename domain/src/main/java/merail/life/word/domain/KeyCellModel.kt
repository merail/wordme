package merail.life.word.domain

data class KeyCellModel(
    val value: String,
    val state: KeyCellStateModel,
)

enum class KeyCellStateModel {
    DEFAULT,
    ABSENT,
    PRESENT,
    CORRECT,
    ;
}