package merail.life.domain

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