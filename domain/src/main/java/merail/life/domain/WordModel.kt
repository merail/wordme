package merail.life.domain

@JvmInline
value class WordModel(
    val value: String,
) {
    companion object
}

val WordModel.Companion.Empty: WordModel
    get() = WordModel("")