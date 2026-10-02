package di

import data.remote.client.SupabaseHttpClient
import data.remote.datasource.RemotePackageDataSource
import data.remote.datasource.RemoteRouteDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.remote.datasource.RemoteWarehouseDataSource
import data.remote.supabase.SupabasePackageDataSourceImpl
import data.remote.supabase.SupabaseRouteDataSourceImpl
import data.remote.supabase.SupabaseVehicleDataSourceImpl
import data.remote.supabase.SupabaseWarehouseDataSourceImpl
import org.koin.dsl.module

val networkModule = module {
    single { SupabaseHttpClient }

    single<RemotePackageDataSource> {
        SupabasePackageDataSourceImpl(get())
    }

    single<RemoteRouteDataSource> {
        SupabaseRouteDataSourceImpl(get())
    }

    single<RemoteVehicleDataSource> {
        SupabaseVehicleDataSourceImpl(get())
    }

    single<RemoteWarehouseDataSource> {
        SupabaseWarehouseDataSourceImpl(get())
    }
}
