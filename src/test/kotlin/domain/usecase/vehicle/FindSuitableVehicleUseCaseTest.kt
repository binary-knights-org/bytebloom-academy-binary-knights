package domain.usecase.vehicle

import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FindSuitableVehicleUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val useCase = FindSuitableVehicleUseCase(vehicleRepository)


    private val origin = Warehouse(
        id = "WH-1",
        name = "Origin Warehouse",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )

    private val otherHub = Warehouse(
        id = "WH-2",
        name = "Other Hub",
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
        weight = 100.0,
        priority = Priority.URGENT,
        originHub = origin,
        destinationHub = destination
    )

    private val packageTwo = Package(
        id = "PKG-2",
        weight = 150.0,
        priority = Priority.STANDARD,
        originHub = origin,
        destinationHub = destination
    )

    private val vehicleWithSpace = Vehicle(
        id = "TRK-1",
        maxCapacityKg = 1000.0,
        costPerKm = 3.0,
        currentHub = origin
    )

    private val vehicleFull = Vehicle(
        id = "TRK-2",
        maxCapacityKg = 200.0,
        costPerKm = 2.0,
        currentHub = origin
    )

    private val vehicleAtOtherHub = Vehicle(
        id = "TRK-3",
        maxCapacityKg = 5000.0,
        costPerKm = 1.0,
        currentHub = otherHub
    )

    @Test
    fun `should return suitable vehicle at origin hub with enough capacity`() = runTest {

        // Given
        vehicleFull.loadPackage(packageOne)

        coEvery {
            vehicleRepository.getAll()
        } returns listOf(vehicleFull, vehicleWithSpace, vehicleAtOtherHub)

        // When
        val result = useCase(listOf(packageOne, packageTwo))

        // Then
        assertEquals(vehicleWithSpace, result)

        coVerify(exactly = 1) {
            vehicleRepository.getAll()
        }
    }

    @Test
    fun `should return null when no vehicle at origin hub has enough capacity`() = runTest {

        // Given
        vehicleFull.loadPackage(packageOne)

        coEvery {
            vehicleRepository.getAll()
        } returns listOf(vehicleFull, vehicleAtOtherHub)

        // When
        val result = useCase(listOf(packageOne, packageTwo))

        // Then
        assertNull(result)

        coVerify(exactly = 1) {
            vehicleRepository.getAll()
        }
    }

    @Test
    fun `should ignore vehicles stationed at a different hub`() = runTest {

        // Given
        coEvery {
            vehicleRepository.getAll()
        } returns listOf(vehicleAtOtherHub)

        // When
        val result = useCase(listOf(packageOne))

        // Then
        assertNull(result)

        coVerify(exactly = 1) {
            vehicleRepository.getAll()
        }
    }
}
