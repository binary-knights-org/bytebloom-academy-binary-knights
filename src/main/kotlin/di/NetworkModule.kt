package di

import data.remote.datasource.RemotePackageDataSource
import data.remote.datasource.RemoteRouteDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.remote.datasource.RemoteWarehouseDataSource
import data.remote.supabase.SupabasePackageDataSourceImpl
import data.remote.supabase.SupabaseRouteDataSourceImpl
import data.remote.supabase.SupabaseVehicleDataSourceImpl
import data.remote.supabase.SupabaseWarehouseDataSourceImpl
import data.remote.ratelimit.RateLimiter
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import io.github.jan.supabase.logging.LogLevel

private const val RATE_LIMIT_MAX_BUCKET_CAPACITY = 10
private const val RATE_LIMIT_REFILL_PER_SECOND = 5.0

val networkModule = module {

    singleOf(::setupSupabaseSpecs)
    singleOf(::setupSupabaseClient)

    single { RateLimiter(RATE_LIMIT_MAX_BUCKET_CAPACITY, RATE_LIMIT_REFILL_PER_SECOND) }

    singleOf(::SupabasePackageDataSourceImpl) bind RemotePackageDataSource::class
    singleOf(::SupabaseRouteDataSourceImpl) bind RemoteRouteDataSource::class
    singleOf(::SupabaseVehicleDataSourceImpl) bind RemoteVehicleDataSource::class
    singleOf(::SupabaseWarehouseDataSourceImpl) bind RemoteWarehouseDataSource::class
}

private fun setupSupabaseClient(supabaseSpecs: SupabaseSpecs): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = supabaseSpecs.supabaseUrl,
        supabaseKey = supabaseSpecs.supabaseKey,
    ) {
        defaultLogLevel = LogLevel.NONE
        install(Postgrest)
    }
}

private fun setupSupabaseSpecs(): SupabaseSpecs{
    val supabaseUrl: String = System.getenv("SUPABASE_URL")?.trim().orEmpty()
    val supabaseKey: String = System.getenv("SUPABASE_KEY")?.trim().orEmpty()

    return SupabaseSpecs(supabaseUrl, supabaseKey)
}

data class SupabaseSpecs(
    val supabaseUrl: String,
    val supabaseKey: String,
)
