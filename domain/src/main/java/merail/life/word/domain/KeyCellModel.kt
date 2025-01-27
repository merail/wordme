package merail.life.word.domain

data class KeyCellModel(
    val value: String,
    val state: KeyStateModel,
)

enum class KeyStateModel {
    DEFAULT,
    ABSENT,
    PRESENT,
    CORRECT,
    ;
}