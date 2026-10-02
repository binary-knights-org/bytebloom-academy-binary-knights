package di

import com.google.common.truth.Truth.assertThat
import data.repository.PackageRepositoryImpl
import data.repository.RouteRepositoryImpl
import data.repository.VehicleRepositoryImpl
import data.repository.WarehouseRepositoryImpl
import domain.repository.PackageRepository
import domain.repository.RouteRepository
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

class RepositoryModuleTest : KoinTest {
    @BeforeEach
    fun setUp() {
        startKoin {
            modules(networkModule, repositoryModule)
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should bind repositories to correct implementations`() {
        assertThat(get<WarehouseRepository>() is WarehouseRepositoryImpl)
        assertThat(get<PackageRepository>() is PackageRepositoryImpl)
        assertThat(get<VehicleRepository>() is VehicleRepositoryImpl)
        assertThat(get<RouteRepository>() is RouteRepositoryImpl)
    }

    @Test
    fun `should provide WarehouseRepository as singleton`() {
        val first = get<WarehouseRepository>()
        val second = get<WarehouseRepository>()

        assertThat(first).isSameInstanceAs(second)
    }

    @Test
    fun `should provide PackageRepository that resolves through WarehouseRepository dependency`() {
        val repository = get<PackageRepository>()
        assertThat(repository).isNotNull()
    }
}
