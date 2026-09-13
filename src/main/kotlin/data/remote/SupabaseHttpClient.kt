package data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object SupabaseHttpClient {
    private val customJson = Json {
        ignoreUnknownKeys = true
    }

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(customJson)
        }

        defaultRequest {
            url(SupabaseConfig.BASE_URL + "/")
            header("apikey", SupabaseConfig.API_KEY)
            header("Authorization", "Bearer ${SupabaseConfig.BEARER_TOKEN}")
            contentType(ContentType.Application.Json)
        }

    }

    suspend fun get(table: String): HttpResponse {
        return client.get(table)
    }

    suspend inline fun <reified T> post(table: String, body: T): HttpResponse {
        return client.post(table) {
            setBody(body)
        }
    }

    suspend inline fun <reified T> patch(table: String, id: String, body: T): HttpResponse {
        return client.patch("$table?id=eq.$id") {
            setBody(body)
        }
    }

    suspend fun delete(table: String, id: String): HttpResponse {
        return client.delete("$table?id=eq.$id")
    }
}
