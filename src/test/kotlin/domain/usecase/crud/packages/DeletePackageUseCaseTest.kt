package domain.usecase.crud.packages

import domain.model.exception.OperationFailedException
import domain.repository.PackageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class DeletePackageUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()
    private val useCase = DeletePackageUseCase(packageRepository)

    @Test
    fun `should return success when package is deleted successfully`() = runBlocking {
        // Given
        val packageId = "PKG-123"
        coEvery { packageRepository.delete(packageId) } returns true

        // When
        val result = useCase(packageId)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { packageRepository.delete(packageId) }
    }

    @Test
    fun `should return failure with OperationFailedException when repository returns false`() = runBlocking {
        // Given
        val packageId = "PKG-123"
        coEvery { packageRepository.delete(packageId) } returns false

        // When
        val result = useCase(packageId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is OperationFailedException)
        coVerify(exactly = 1) { packageRepository.delete(packageId) }
    }

    @Test
    fun `should return failure when repository throws an exception`() = runBlocking {
        // Given
        val packageId = "PKG-123"
        val exception = RuntimeException("Database error")
        coEvery { packageRepository.delete(packageId) } throws exception

        // When
        val result = useCase(packageId)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 1) { packageRepository.delete(packageId) }
    }
}
