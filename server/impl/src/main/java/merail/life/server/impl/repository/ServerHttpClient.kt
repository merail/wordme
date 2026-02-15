package merail.life.server.impl.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import merail.life.server.impl.BuildConfig
import javax.inject.Inject

internal class ServerHttpClient @Inject constructor() {

    val httpClient = HttpClient(OkHttp) {
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
            url {
                protocol = URLProtocol.HTTPS
                host = BuildConfig.DOMAIN_URL
            }
        }
    }
}
