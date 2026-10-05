package di

import com.google.common.truth.Truth.assertThat
import data.remote.datasource.RemotePackageDataSource
import data.remote.datasource.RemoteRouteDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.remote.datasource.RemoteWarehouseDataSource
import data.remote.supabase.SupabasePackageDataSourceImpl
import data.remote.supabase.SupabaseRouteDataSourceImpl
import data.remote.supabase.SupabaseVehicleDataSourceImpl
import data.remote.supabase.SupabaseWarehouseDataSourceImpl
import io.github.jan.supabase.SupabaseClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

class NetworkModuleTest : KoinTest {

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(networkModule)
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should provide SupabaseClient as singleton`() {
        val first = get<SupabaseClient>()
        val second = get<SupabaseClient>()

        assertThat(first).isSameInstanceAs(second)
    }

    @Test
    fun `should bind remote data sources to correct implementations`() {
        assertThat(get<RemotePackageDataSource>()).isInstanceOf(SupabasePackageDataSourceImpl::class.java)
        assertThat(get<RemoteRouteDataSource>()).isInstanceOf(SupabaseRouteDataSourceImpl::class.java)
        assertThat(get<RemoteVehicleDataSource>()).isInstanceOf(SupabaseVehicleDataSourceImpl::class.java)
        assertThat(get<RemoteWarehouseDataSource>()).isInstanceOf(SupabaseWarehouseDataSourceImpl::class.java)
    }
}
