package data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object SupabaseHttpClientFactory {

    fun create(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }

            defaultRequest {
                url(SupabaseConfig.BASE_URL + "/")
                header("apikey", SupabaseConfig.API_KEY)
                header(
                    "Authorization",
                    "Bearer ${SupabaseConfig.BEARER_TOKEN}"
                )
                contentType(ContentType.Application.Json)
            }
        }
    }
}
