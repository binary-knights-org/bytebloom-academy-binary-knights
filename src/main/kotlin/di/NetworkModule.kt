package di

import data.remote.datasource.RemotePackageDataSource
import data.remote.datasource.RemoteRouteDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.remote.datasource.RemoteWarehouseDataSource
import data.remote.supabase.SupabasePackageDataSourceImpl
import data.remote.supabase.SupabaseRouteDataSourceImpl
import data.remote.supabase.SupabaseVehicleDataSourceImpl
import data.remote.supabase.SupabaseWarehouseDataSourceImpl
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {

    singleOf(::setupSupabaseSpecs)
    singleOf(::setupSupabaseClient)

    singleOf(::SupabasePackageDataSourceImpl) bind RemotePackageDataSource::class
    singleOf(::SupabaseRouteDataSourceImpl) bind RemoteRouteDataSource::class
    singleOf(::SupabaseVehicleDataSourceImpl) bind RemoteVehicleDataSource::class
    singleOf(::SupabaseWarehouseDataSourceImpl) bind RemoteWarehouseDataSource::class
}

private fun setupSupabaseClient(supabaseSpecs: SupabaseSpecs): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = supabaseSpecs.supabaseUrl,
        supabaseKey = supabaseSpecs.supabaseKey,
    ){
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
