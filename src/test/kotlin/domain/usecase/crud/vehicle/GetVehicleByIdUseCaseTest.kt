package domain.usecase.crud.vehicle

import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse
import domain.model.exception.ResourceNotFoundException
import domain.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetVehicleByIdUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val useCase = GetVehicleByIdUseCase(
        vehicleRepository
    )

    private val origin = Warehouse(
        id = "WH-1",
        name = "Origin Warehouse",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )


    private val vehicleItem = Vehicle(
        id = "TRK-1",
        currentHub = origin,
        maxCapacityKg = 8500.0 ,
        costPerKm = 4.2
        )


    @Test
    fun `should return vehicle when vehicle exists`() = runTest {
        // Given
        coEvery {
            vehicleRepository.getById("TRK-1")
        } returns vehicleItem

        // When
        val result = useCase("TRK-1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(vehicleItem, result.getOrNull())

        coVerify(exactly = 1) {
            vehicleRepository.getById("TRK-1")
        }
    }

    @Test
    fun `should return ResourceNotFoundException when vehicle does not exist`() = runTest {
        // Given
        coEvery {
            vehicleRepository.getById("TRK-999")
        } returns null

        // When
        val result = useCase("TRK-999")

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is ResourceNotFoundException
        )

        coVerify(exactly = 1) {
            vehicleRepository.getById("TRK-999")
        }
    }


    @Test
    fun `should return repository exception when fetching package fails`() = runTest {
        // Given
        val exception = RuntimeException("Database error")

        coEvery {
            vehicleRepository.getById("TRK-1")
        } throws exception

        // When
        val result = useCase("TRK-1")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            vehicleRepository.getById("TRK-1")
        }
    }
}
