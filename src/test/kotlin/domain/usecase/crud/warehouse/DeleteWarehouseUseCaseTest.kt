package domain.usecase.crud.warehouse

import domain.model.exception.OperationFailedException
import domain.repository.WarehouseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DeleteWarehouseUseCaseTest {

    private val warehouseRepository = mockk<WarehouseRepository>()

    private val useCase = DeleteWarehouseUseCase(
        warehouseRepository = warehouseRepository
    )

    @Test
    fun `should delete warehouse successfully when repository deletion succeeds`() = runTest {
        // Given
        val warehouseId = "WH-001"

        coEvery {
            warehouseRepository.delete(warehouseId)
        } returns true

        // When
        val result = useCase(warehouseId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())

        coVerify(exactly = 1) {
            warehouseRepository.delete(warehouseId)
        }
    }

    @Test
    fun `should return operation failed exception when repository deletion returns false`() = runTest {
        // Given
        val warehouseId = "WH-001"

        coEvery {
            warehouseRepository.delete(warehouseId)
        } returns false

        // When
        val result = useCase(warehouseId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is OperationFailedException
        )

        coVerify(exactly = 1) {
            warehouseRepository.delete(warehouseId)
        }
    }

    @Test
    fun `should return repository exception when deletion fails`() = runTest {
        // Given
        val warehouseId = "WH-001"
        val exception = RuntimeException("Database error")

        coEvery {
            warehouseRepository.delete(warehouseId)
        } throws exception

        // When
        val result = useCase(warehouseId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            warehouseRepository.delete(warehouseId)
        }
    }
}
