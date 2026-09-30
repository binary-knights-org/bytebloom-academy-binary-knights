package domain.usecase.crud.packages

import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.ResourceNotFoundException
import domain.repository.PackageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetPackageByIdUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()

    private val useCase = GetPackageByIdUseCase(
        packageRepository
    )

    private val origin = Warehouse(
        id = "WH-1",
        name = "Origin Warehouse",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )

    private val destination = Warehouse(
        id = "WH-2",
        name = "Destination Warehouse",
        regionalZone = RegionalZone.SOUTH,
        latitude = 31.50,
        longitude = 34.47
    )

    private val packageItem = Package(
        id = "PKG-1",
        weight = 10.0,
        priority = Priority.URGENT,
        originHub = origin,
        destinationHub = destination
    )

    @Test
    fun `should return package when package exists`() = runTest {
        // Given
        coEvery {
            packageRepository.getById("PKG-1")
        } returns packageItem

        // When
        val result = useCase("PKG-1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(packageItem, result.getOrNull())

        coVerify(exactly = 1) {
            packageRepository.getById("PKG-1")
        }
    }

    @Test
    fun `should return ResourceNotFoundException when package does not exist`() = runTest {
        // Given
        coEvery {
            packageRepository.getById("PKG-999")
        } returns null

        // When
        val result = useCase("PKG-999")

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is ResourceNotFoundException
        )

        coVerify(exactly = 1) {
            packageRepository.getById("PKG-999")
        }
    }

    @Test
    fun `should return repository exception when fetching package fails`() = runTest {
        // Given
        val exception = RuntimeException("Database error")

        coEvery {
            packageRepository.getById("PKG-1")
        } throws exception

        // When
        val result = useCase("PKG-1")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            packageRepository.getById("PKG-1")
        }
    }
}
