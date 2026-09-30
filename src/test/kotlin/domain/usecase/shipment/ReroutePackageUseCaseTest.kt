package domain.usecase.shipment

import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.DomainException.Companion.RESOURCE_NOT_FOUND
import domain.model.exception.ResourceNotFoundException
import domain.repository.PackageRepository
import domain.repository.WarehouseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ReroutePackageUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()
    private val warehouseRepository = mockk<WarehouseRepository>()

    private val useCase = ReroutePackageUseCase(
        packageRepository,
        warehouseRepository
    )

    private val origin = Warehouse(
        id = "WH-1",
        name = "Origin Warehouse",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )

    private val oldDestination = Warehouse(
        id = "WH-2",
        name = "Old Destination",
        regionalZone = RegionalZone.CENTRAL,
        latitude = 31.80,
        longitude = 35.20
    )

    private val newDestination = Warehouse(
        id = "WH-3",
        name = "New Destination",
        regionalZone = RegionalZone.SOUTH,
        latitude = 31.50,
        longitude = 34.47
    )

    private val packageItem = Package(
        id = "PKG-1",
        weight = 10.0,
        priority = Priority.URGENT,
        originHub = origin,
        destinationHub = oldDestination
    )

    @Test
    fun `should reroute package successfully`() = runBlocking {

        // Given
        oldDestination.addPackage(packageItem)

        coEvery {
            packageRepository.getAll()
        } returns listOf(packageItem)

        coEvery {
            warehouseRepository.getAll()
        } returns listOf(origin, oldDestination, newDestination)

        // When
        useCase("PKG-1", "WH-3")

        // Then
        assertEquals(emptyList(), oldDestination.cargoQueue)
        assertEquals(1, newDestination.cargoQueue.size)
        assertEquals(
            "WH-3",
            newDestination.cargoQueue.first().destinationHub.id
        )

        coVerify(exactly = 1) {
            packageRepository.getAll()
        }

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }
    }

    @Test
    fun `should fail when package does not exist`() = runBlocking {

        // Given
        coEvery {
            packageRepository.getAll()
        } returns emptyList()

        // When / Then
        val exception = assertFailsWith<ResourceNotFoundException> {
            useCase("PKG-999", "WH-3")
        }

        assertEquals(
            RESOURCE_NOT_FOUND,
            exception.message
        )

        coVerify(exactly = 1) {
            packageRepository.getAll()
        }

        coVerify(exactly = 0) {
            warehouseRepository.getAll()
        }
    }

    @Test
    fun `should fail when new destination does not exist`() = runBlocking {

        // Given
        coEvery {
            packageRepository.getAll()
        } returns listOf(packageItem)

        coEvery {
            warehouseRepository.getAll()
        } returns listOf(origin, oldDestination)

        // When / Then
        val exception = assertFailsWith<ResourceNotFoundException> {
            useCase("PKG-1", "WH-999")
        }

        assertEquals(
            RESOURCE_NOT_FOUND,
            exception.message
        )

        coVerify(exactly = 1) {
            packageRepository.getAll()
        }

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }
    }
}
