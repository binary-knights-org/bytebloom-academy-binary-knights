package domain.usecase.crud.vehicle

import domain.model.exception.OperationFailedException
import domain.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteVehicleUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val useCase = DeleteVehicleUseCase(
        vehicleRepository
    )


    @Test
    fun `should delete vehicle successfully`() = runTest {
        // Given
        coEvery {
            vehicleRepository.delete("TRK-1")
        } returns true

        // When
        val result = useCase("TRK-1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())

        coVerify(exactly = 1) {
            vehicleRepository.delete("TRK-1")
        }
    }

    @Test
    fun `should return OperationFailedException when deletion returns false`() = runTest {
        // Given
        coEvery {
        vehicleRepository.delete("TRK-1")
        } returns false

        // When
        val result = useCase("TRK-1")

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is OperationFailedException
        )

        coVerify(exactly = 1) {
            vehicleRepository.delete("TRK-1")
        }
    }

    @Test
    fun `should return repository exception when deletion fails`() = runTest {

        // Given
        val exception = RuntimeException("Database error")

        coEvery {
            vehicleRepository.delete("TRK-1")
        } throws exception


        // When
        val result = useCase("TRK-1")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            vehicleRepository.delete("TRK-1")
        }
    }
}
