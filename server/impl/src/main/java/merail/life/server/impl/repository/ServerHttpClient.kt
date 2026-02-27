package merail.life.server.impl.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import merail.life.server.impl.BuildConfig
import javax.inject.Inject

internal class ServerHttpClient @Inject constructor() {

    companion object {
        private const val ACCESS_TOKEN = "X-WordMe-Token"
    }

    val httpClient = HttpClient(OkHttp) {
        expectSuccess = true

        install(Logging) {
            logger = Logger.ANDROID
            level = if (BuildConfig.DEBUG) LogLevel.INFO else LogLevel.NONE
        }

        install(ContentNegotiation) {
            json(
                json = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                },
            )
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            retryOnException(maxRetries = 3, retryOnTimeout = true)
            exponentialDelay()
        }

        defaultRequest {
            header(ACCESS_TOKEN, BuildConfig.ACCESS_TOKEN)

            url {
                protocol = URLProtocol.HTTPS
                host = BuildConfig.DOMAIN_URL
            }
        }
    }
}
