package domain.usecase.crud.packages

import domain.repository.PackageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class DeletePackageUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()

    private val useCase = DeletePackageUseCase(
        packageRepository
    )

    @Test
    fun `should delete package successfully`() = runBlocking {

        // Given
        coEvery {
            packageRepository.delete("PKG-1")
        } returns true

        // When
        val result = useCase("PKG-1")

        // Then
        assertTrue(result.isSuccess)

        coVerify(exactly = 1) {
            packageRepository.delete("PKG-1")
        }
    }

    @Test
    fun `should fail when package deletion returns false`() = runBlocking {

        // Given
        coEvery {
            packageRepository.delete("PKG-1")
        } returns false

        // When
        val result = useCase("PKG-1")

        // Then
        assertTrue(result.isFailure)

        coVerify(exactly = 1) {
            packageRepository.delete("PKG-1")
        }
    }
}
