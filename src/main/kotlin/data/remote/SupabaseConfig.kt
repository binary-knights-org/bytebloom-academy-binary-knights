package data.remote

import java.io.File
import java.util.Properties

object SupabaseConfig {
    val BASE_URL: String = System.getenv("SUPABASE_URL") ?: ""
    val API_KEY: String = System.getenv("SUPABASE_KEY") ?: ""
    val BEARER_TOKEN: String = API_KEY
}
