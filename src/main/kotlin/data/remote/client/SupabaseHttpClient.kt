package data.remote.client

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

object SupabaseHttpClient {

    val client = SupabaseHttpClientFactory.create()

    suspend fun get(table: String): HttpResponse {
        return client.get(table)
    }

    suspend inline fun <reified T> post(
        table: String,
        body: T
    ): HttpResponse {
        return client.post(table) {
            setBody(body)
        }
    }

    suspend inline fun <reified T> patch(
        table: String,
        id: String,
        body: T,
        primaryKey: String = "id"
    ): HttpResponse {
        return client.patch("$table?$primaryKey=eq.$id") {
            setBody(body)
        }
    }

    suspend fun delete(
        table: String,
        id: String,
        primaryKey: String = "id"
    ): HttpResponse {
        return client.delete("$table?$primaryKey=eq.$id")
    }
}
