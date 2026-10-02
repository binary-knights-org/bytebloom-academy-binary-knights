package di

import com.google.common.truth.Truth.assertThat
import domain.validator.packages.CreatePackageValidator
import domain.validator.packages.UpdatePackageValidator
import domain.validator.routes.CreateRouteValidator
import domain.validator.routes.UpdateRouteValidator
import domain.validator.vehicle.CreateVehicleValidator
import domain.validator.vehicle.UpdateVehicleValidator
import domain.validator.warehouse.CreateWarehouseValidator
import domain.validator.warehouse.UpdateWarehouseValidator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

class ValidatorModuleTest : KoinTest {

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(validatorModule)
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should resolve all validators`() {
        assertThat(get<CreatePackageValidator>()).isNotNull()
        assertThat(get<UpdatePackageValidator>()).isNotNull()

        assertThat(get<CreateRouteValidator>()).isNotNull()
        assertThat(get<UpdateRouteValidator>()).isNotNull()

        assertThat(get<CreateVehicleValidator>()).isNotNull()
        assertThat(get<UpdateVehicleValidator>()).isNotNull()

        assertThat(get<CreateWarehouseValidator>()).isNotNull()
        assertThat(get<UpdateWarehouseValidator>()).isNotNull()
    }

    @Test
    fun `should provide validators as singleton`() {
        val first = get<CreatePackageValidator>()
        val second = get<CreatePackageValidator>()

        assertThat(first).isSameInstanceAs(second)
    }
}