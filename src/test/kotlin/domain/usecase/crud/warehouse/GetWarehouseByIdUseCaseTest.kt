package domain.usecase.crud.warehouse

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.ResourceNotFoundException
import domain.repository.WarehouseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetWarehouseByIdUseCaseTest {

    private val warehouseRepository = mockk<WarehouseRepository>()

    private val useCase = GetWarehouseByIdUseCase(
        warehouseRepository = warehouseRepository
    )

    private val warehouse = Warehouse(
        id = "WH-001",
        name = "Hub-001",
        regionalZone = RegionalZone.CENTRAL,
        latitude = 37.91,
        longitude = -88.46
    )

    @Test
    fun `should return warehouse when warehouse exists`() = runTest {
        // Given
        coEvery {
            warehouseRepository.getById(warehouse.id)
        } returns warehouse

        // When
        val result = useCase(warehouse.id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(warehouse, result.getOrNull())

        coVerify(exactly = 1) {
            warehouseRepository.getById(warehouse.id)
        }
    }

    @Test
    fun `should return resource not found failure when warehouse does not exist`() = runTest {
        // Given
        val warehouseId = "WH-999"

        coEvery {
            warehouseRepository.getById(warehouseId)
        } returns null

        // When
        val result = useCase(warehouseId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is ResourceNotFoundException
        )

        coVerify(exactly = 1) {
            warehouseRepository.getById(warehouseId)
        }
    }

    @Test
    fun `should return repository exception when get warehouse fails`() = runTest {
        // Given
        val warehouseId = "WH-001"
        val exception = RuntimeException("Database error")

        coEvery {
            warehouseRepository.getById(warehouseId)
        } throws exception

        // When
        val result = useCase(warehouseId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            warehouseRepository.getById(warehouseId)
        }
    }
}
