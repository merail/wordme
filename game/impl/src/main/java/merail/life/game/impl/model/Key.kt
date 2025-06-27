package merail.life.game.impl.model

internal enum class Key(val value: String) {
    А("А"),
    Б("Б"),
    В("В"),
    Г("Г"),
    Д("Д"),
    Е("Е"),
    Ж("Ж"),
    З("З"),
    И("И"),
    Й("Й"),
    К("К"),
    Л("Л"),
    М("М"),
    Н("Н"),
    О("О"),
    П("П"),
    Р("Р"),
    С("С"),
    Т("Т"),
    У("У"),
    Ф("Ф"),
    Х("Х"),
    Ц("Ц"),
    Ч("Ч"),
    Ш("Ш"),
    Щ("Щ"),
    Ъ("Ъ"),
    Ы("Ы"),
    Ь("Ь"),
    Э("Э"),
    Ю("Ю"),
    Я("Я"),
    DEL("DEL"),
    OK("OK"),
    EMPTY(""),
    ;

    companion object {
        fun getKeyFromValue(
            value: String,
        ) = entries.find {
            it.value == value
        } ?: EMPTY
    }
}