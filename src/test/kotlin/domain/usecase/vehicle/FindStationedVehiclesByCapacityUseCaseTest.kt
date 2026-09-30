package domain.usecase.vehicle

import com.google.common.truth.Truth.assertThat
import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.WarehouseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

class FindStationedVehiclesByCapacityUseCaseTest {

    private val warehouseRepository = mockk<WarehouseRepository>()
    private val useCase = FindStationedVehiclesByCapacityUseCase(warehouseRepository)

    private val warehouse = Warehouse(
        id = "WH-1",
        name = "Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    )

    private val vehicle1 = Vehicle(
        maxCapacityKg = 50.0,
        costPerKm = 1.0,
        currentHub = warehouse
    )

    private val vehicle2 = Vehicle(
        maxCapacityKg = 150.0,
        costPerKm = 1.0,
        currentHub = warehouse
    )

    @Test
    fun `should return vehicles that meet minimum capacity`() = runTest {
        // Given
        warehouse.addVehicle(vehicle1)
        warehouse.addVehicle(vehicle2)

        coEvery { warehouseRepository.getAll() } returns listOf(warehouse)

        // When
        val result = useCase("WH-1", 100.0)

        // Then
        assertThat(result).containsExactly(vehicle2)
    }

    @Test
    fun `should return empty list when warehouse does not exist`() = runTest {
        // Given
        coEvery { warehouseRepository.getAll() } returns emptyList()

        // When
        val result = useCase("WH-999", 80.0)

        // Then
        assertThat(result).isEmpty()
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
                useCase("WH-1", 100.0)
            }
        }

        // Then
        assertThat(thrown.message)
            .isEqualTo("Failed to get warehouses")
    }
}