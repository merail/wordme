package merail.life.core.log

interface IWordMeLogger {
    fun d(tag: String, msg: String)
    fun w(tag: String, msg: String, th: Throwable? = null)
}
