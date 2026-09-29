package domain.usecase.shipment

import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Warehouse
import domain.repository.PackageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class FindPackagesByOriginUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()

    private val useCase = FindPackagesByOriginUseCase(
        packageRepository
    )

    private val origin = Warehouse(
        id = "WH-1",
        name = "Origin Warehouse",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )

    private val anotherOrigin = Warehouse(
        id = "WH-2",
        name = "Another Origin",
        regionalZone = RegionalZone.CENTRAL,
        latitude = 31.80,
        longitude = 35.20
    )

    private val destination = Warehouse(
        id = "WH-3",
        name = "Destination Warehouse",
        regionalZone = RegionalZone.SOUTH,
        latitude = 31.50,
        longitude = 34.47
    )

    private val packageOne = Package(
        id = "PKG-1",
        weight = 10.0,
        priority = Priority.URGENT,
        originHub = origin,
        destinationHub = destination
    )

    private val packageTwo = Package(
        id = "PKG-2",
        weight = 15.0,
        priority = Priority.STANDARD,
        originHub = origin,
        destinationHub = destination
    )

    private val packageThree = Package(
        id = "PKG-3",
        weight = 20.0,
        priority = Priority.LOW,
        originHub = anotherOrigin,
        destinationHub = destination
    )

    @Test
    fun `should return packages from requested origin`() = runBlocking {

        // Given
        coEvery {
            packageRepository.getAll()
        } returns listOf(packageOne, packageTwo, packageThree)

        // When
        val result = useCase(origin)

        // Then
        assertEquals(
            listOf(packageOne, packageTwo),
            result
        )

        coVerify(exactly = 1) {
            packageRepository.getAll()
        }
    }

    @Test
    fun `should return empty list when no packages match origin`() = runBlocking {

        // Given
        coEvery {
            packageRepository.getAll()
        } returns listOf(packageThree)

        // When
        val result = useCase(origin)

        // Then
        assertEquals(emptyList(), result)

        coVerify(exactly = 1) {
            packageRepository.getAll()
        }
    }
}
