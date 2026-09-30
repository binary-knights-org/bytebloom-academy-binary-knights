package domain.usecase.vehicle

import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AddVehicleToHubUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val useCase = AddVehicleToHubUseCase(vehicleRepository)

    private val hub = Warehouse(
        id = "WH-1",
        name = "Main Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )

    private val vehicle = Vehicle(
        id = "TRK-1",
        maxCapacityKg = 5000.0,
        costPerKm = 3.0,
        currentHub = hub
    )



    @Test
    fun `should add vehicle successfully`() = runTest {

        // Given
        coEvery {
            vehicleRepository.create(vehicle)
        } returns true

        // When
        val result = useCase(vehicle)

        // Then
        assertTrue(result)

        coVerify(exactly = 1) {
            vehicleRepository.create(vehicle)
        }
    }

    @Test
    fun `should fail to add vehicle when repository fails`() = runTest {

        // Given
        coEvery {
            vehicleRepository.create(vehicle)
        } returns false

        // When
        val result = useCase(vehicle)

        // Then
        assertFalse(result)

        coVerify(exactly = 1) {
            vehicleRepository.create(vehicle)
        }
    }
}
