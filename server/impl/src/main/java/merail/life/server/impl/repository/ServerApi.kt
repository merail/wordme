package merail.life.server.impl.repository

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

internal class ServerApi @Inject constructor(
    private val serverHttpClient: ServerHttpClient,
) {

    suspend fun getDayWord(
        randomWordId: Int,
    ): String = serverHttpClient.httpClient.get("dayWord") {
        parameter("randomWordId", randomWordId)
    }.body()

    suspend fun isWordExist(
        enteredWord: String,
    ): Boolean = serverHttpClient.httpClient.get("isWordExist") {
        parameter("enteredWord", enteredWord)
    }.body()

    suspend fun getGameCountdownStartDate(): String = serverHttpClient.httpClient.get(
        urlString = "gameCountdownStartDate",
    ).body()
}
