package merail.life.word.domain

@JvmInline
value class WordModel(
    val value: String,
) {
    companion object
}

val WordModel.Companion.Empty: WordModel
    get() = WordModel("")

val WordModel.isEmpty: Boolean
    get() = value.isEmpty()

fun WordModel?.orEmpty() = this ?: WordModel("")