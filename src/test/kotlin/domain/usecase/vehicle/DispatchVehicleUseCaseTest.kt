package domain.usecase.vehicle

import com.google.common.truth.Truth.assertThat
import domain.model.Package
import domain.model.Priority
import domain.model.Vehicle
import domain.model.Warehouse
import domain.model.RegionalZone
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DispatchVehicleUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val warehouseRepository = mockk<WarehouseRepository>()
    private val useCase = DispatchVehicleUseCase(vehicleRepository, warehouseRepository)

    private val origin = Warehouse(
        name = "Origin",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    )

    private val destination = Warehouse(
        name = "Destination",
        regionalZone = RegionalZone.SOUTH,
        latitude = 1.0,
        longitude = 1.0
    )

    private val warehouse = Warehouse(
        id = "WH-1",
        name = "Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    )

    private val vehicle = Vehicle(
        id = "TRK-1",
        maxCapacityKg = 50.0,
        costPerKm = 1.0,
        currentHub = warehouse
    )

    private val package1 = Package(
        weight = 10.0,
        priority = Priority.STANDARD,
        originHub = origin,
        destinationHub = destination
    )

    @Test
    fun `should dispatch packages that vehicle can load`() = runTest {
        // Given
        warehouse.addPackage(package1)

        coEvery { warehouseRepository.getAll() } returns listOf(warehouse)
        coEvery { vehicleRepository.getAll() } returns listOf(vehicle)

        // When
        val loaded = useCase(vehicle, warehouse)

        // Then
        assertThat(loaded).containsExactly(package1)
    }

    @Test
    fun `should return empty list when warehouse does not exist`() = runTest {
        // Given
        coEvery { warehouseRepository.getAll() } returns emptyList()
        coEvery { vehicleRepository.getAll() } returns listOf(vehicle)

        // When
        val loaded = useCase(vehicle, warehouse)

        // Then
        assertThat(loaded).isEmpty()
    }

    @Test
    fun `should propagate exception when getting warehouses fails`() = runTest {
        // Given
        val expectedException = IllegalStateException("Failed to get warehouses")

        coEvery { warehouseRepository.getAll() } throws expectedException

        // When
        val thrown = Assertions.assertThrows(
            IllegalStateException::class.java
        ) {
            runBlocking {
                useCase(vehicle, warehouse)
            }
        }

        // Then
        assertThat(thrown.message)
            .isEqualTo("Failed to get warehouses")
    }
}