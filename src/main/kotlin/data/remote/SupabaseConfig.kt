package data.remote

import java.io.File
import java.util.Properties

object SupabaseConfig {

    private val properties = Properties().apply {
        val file = File("local.properties")
        if (file.exists()) {
            load(file.inputStream())
        }
    }

    val BASE_URL: String = properties.getProperty("SUPABASE_URL") ?: ""
    val API_KEY: String = properties.getProperty("SUPABASE_KEY") ?: ""
    val BEARER_TOKEN: String = API_KEY
}
