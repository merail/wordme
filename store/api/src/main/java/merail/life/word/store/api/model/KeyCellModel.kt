package merail.life.word.store.api.model

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