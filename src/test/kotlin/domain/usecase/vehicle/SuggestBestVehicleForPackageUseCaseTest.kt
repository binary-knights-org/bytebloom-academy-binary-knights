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

class SuggestBestVehicleForPackageUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val useCase = SuggestBestVehicleForPackageUseCase(
        vehicleRepository
    )

    private val hub = Warehouse(
        id = "WH-1",
        name = "Main Hub",
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

    private val existingHubLoad = Package(
        id = "PKG-EXISTING",
        weight = 500.0,
        priority = Priority.STANDARD,
        originHub = hub,
        destinationHub = destination
    )

    private val packageToAssign = Package(
        id = "PKG-1",
        weight = 100.0,
        priority = Priority.URGENT,
        originHub = hub,
        destinationHub = destination
    )

    private val cheapVehicle = Vehicle(
        id = "TRK-1",
        maxCapacityKg = 700.0,
        costPerKm = 2.0,
        currentHub = hub
    )

    private val expensiveVehicle = Vehicle(
        id = "TRK-2",
        maxCapacityKg = 700.0,
        costPerKm = 5.0,
        currentHub = hub
    )

    private val insufficientCapacityVehicle = Vehicle(
        id = "TRK-3",
        maxCapacityKg = 550.0,
        costPerKm = 1.0,
        currentHub = hub
    )

    @Test
    fun `should suggest cheapest vehicle with sufficient remaining capacity`() = runTest {

        // Given
        hub.addPackage(existingHubLoad)

        coEvery {
            vehicleRepository.getAll()
        } returns listOf(expensiveVehicle, cheapVehicle, insufficientCapacityVehicle)

        // When
        val result = useCase(packageToAssign)

        // Then
        assertEquals(cheapVehicle, result)

        coVerify(exactly = 1) {
            vehicleRepository.getAll()
        }
    }

    @Test
    fun `should exclude the cheapest vehicle if it lacks remaining capacity`() = runTest {

        // Given
        hub.addPackage(existingHubLoad)

        coEvery {
            vehicleRepository.getAll()
        } returns listOf(cheapVehicle, insufficientCapacityVehicle)

        // When
        val result = useCase(packageToAssign)

        // Then
        assertEquals(cheapVehicle, result)

        coVerify(exactly = 1) {
            vehicleRepository.getAll()
        }
    }

    @Test
    fun `should return null when no vehicle has sufficient remaining capacity`() = runTest {

        // Given
        hub.addPackage(existingHubLoad)

        coEvery {
            vehicleRepository.getAll()
        } returns listOf(insufficientCapacityVehicle)

        // When
        val result = useCase(packageToAssign)

        // Then
        assertNull(result)

        coVerify(exactly = 1) {
            vehicleRepository.getAll()
        }
    }
}
