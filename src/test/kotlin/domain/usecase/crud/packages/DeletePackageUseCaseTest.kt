package domain.usecase.crud.packages

import domain.model.exception.OperationFailedException
import domain.repository.PackageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeletePackageUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()

    private val useCase = DeletePackageUseCase(
        packageRepository
    )

    @Test
    fun `should delete package successfully`() = runTest {
        // Given
        coEvery {
            packageRepository.delete("PKG-1")
        } returns true

        // When
        val result = useCase("PKG-1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())

        coVerify(exactly = 1) {
            packageRepository.delete("PKG-1")
        }
    }

    @Test
    fun `should return OperationFailedException when deletion returns false`() = runTest {
        // Given
        coEvery {
            packageRepository.delete("PKG-1")
        } returns false

        // When
        val result = useCase("PKG-1")

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is OperationFailedException
        )

        coVerify(exactly = 1) {
            packageRepository.delete("PKG-1")
        }
    }

    @Test
    fun `should return repository exception when deletion fails`() = runTest {
        // Given
        val exception = RuntimeException("Database error")

        coEvery {
            packageRepository.delete("PKG-1")
        } throws exception

        // When
        val result = useCase("PKG-1")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            packageRepository.delete("PKG-1")
        }
    }
}
