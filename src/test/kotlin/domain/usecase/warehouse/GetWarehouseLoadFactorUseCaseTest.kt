package domain.usecase.warehouse

import domain.model.Package
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.WarehouseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class GetWarehouseLoadFactorUseCaseTest {

    private val warehouseRepository = mockk<WarehouseRepository>()

    private val useCase = GetWarehouseLoadFactorUseCase(
        warehouseRepository = warehouseRepository
    )

    @Test
    fun `should return zero when warehouse does not exist`() = runTest {
        // Given
        val warehouseId = "WH-999"

        coEvery {
            warehouseRepository.getAll()
        } returns emptyList()

        // When
        val loadFactor = useCase(warehouseId)

        // Then
        assertEquals(0.0, loadFactor)

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }
    }

    @Test
    fun `should return zero when warehouse has no fleet capacity`() = runTest {
        // Given
        val warehouseId = "WH-001"
        val warehouse = mockk<Warehouse>()

        every {
            warehouse.id
        } returns warehouseId

        every {
            warehouse.cargoQueue
        } returns emptyList()

        every {
            warehouse.stationedVehicles
        } returns emptyList()

        coEvery {
            warehouseRepository.getAll()
        } returns listOf(warehouse)

        // When
        val loadFactor = useCase(warehouseId)

        // Then
        assertEquals(0.0, loadFactor)

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }
    }

    @Test
    fun `should calculate load factor correctly`() = runTest {
        // Given
        val warehouseId = "WH-001"
        val warehouse = mockk<Warehouse>()

        val packageOne = mockk<Package>()
        val packageTwo = mockk<Package>()

        every { packageOne.weight } returns 100.0
        every { packageTwo.weight } returns 50.0

        val vehicleOne = mockk<Vehicle>()
        val vehicleTwo = mockk<Vehicle>()

        every { vehicleOne.maxCapacityKg } returns 200.0
        every { vehicleTwo.maxCapacityKg } returns 100.0

        every { warehouse.id } returns warehouseId

        every {
            warehouse.cargoQueue
        } returns listOf(packageOne, packageTwo)

        every {
            warehouse.stationedVehicles
        } returns listOf(vehicleOne, vehicleTwo)

        coEvery {
            warehouseRepository.getAll()
        } returns listOf(warehouse)

        // When
        val loadFactor = useCase(warehouseId)

        // Then
        assertEquals(0.5, loadFactor)

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }
    }

    @Test
    fun `should include all cargo and vehicle capacities in calculation`() = runTest {
        // Given
        val warehouseId = "WH-001"
        val warehouse = mockk<Warehouse>()

        val packageOne = mockk<Package>()
        val packageTwo = mockk<Package>()
        val packageThree = mockk<Package>()

        every { packageOne.weight } returns 120.0
        every { packageTwo.weight } returns 80.0
        every { packageThree.weight } returns 50.0

        val vehicleOne = mockk<Vehicle>()
        val vehicleTwo = mockk<Vehicle>()
        val vehicleThree = mockk<Vehicle>()

        every { vehicleOne.maxCapacityKg } returns 200.0
        every { vehicleTwo.maxCapacityKg } returns 150.0
        every { vehicleThree.maxCapacityKg } returns 100.0

        every { warehouse.id } returns warehouseId

        every {
            warehouse.cargoQueue
        } returns listOf(packageOne, packageTwo, packageThree)

        every {
            warehouse.stationedVehicles
        } returns listOf(vehicleOne, vehicleTwo, vehicleThree)

        coEvery {
            warehouseRepository.getAll()
        } returns listOf(warehouse)

        // When
        val loadFactor = useCase(warehouseId)

        // Then
        assertEquals(250.0 / 450.0, loadFactor)

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }
    }

    @Test
    fun `should propagate repository exception`() = runTest {
        // Given
        val repositoryException = IllegalStateException(
            "Failed to load warehouses"
        )

        coEvery {
            warehouseRepository.getAll()
        } throws repositoryException

        // When
        val exception = assertFailsWith<IllegalStateException> {
            useCase("WH-001")
        }

        // Then
        assertEquals(repositoryException, exception)

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }
    }
}
