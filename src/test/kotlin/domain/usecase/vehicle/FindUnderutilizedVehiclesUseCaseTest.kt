package domain.usecase.vehicle

import com.google.common.truth.Truth.assertThat
import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class FindUnderutilizedVehiclesUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val useCase = FindUnderutilizedVehiclesUseCase(vehicleRepository)

    private val warehouse = Warehouse(
        name = "Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    )

    private val vehicle = Vehicle(
        maxCapacityKg = 100.0,
        costPerKm = 1.0,
        currentHub = warehouse
    )

    @Test
    fun `should return underutilized vehicles`() = runTest {
        // Given
        coEvery { vehicleRepository.getAll() } returns listOf(vehicle)

        // When
        val result = useCase(0.5)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should not return vehicle when cargo queue is empty`() = runTest {
        // Given
        coEvery { vehicleRepository.getAll() } returns listOf(vehicle)

        // When
        val result = useCase(0.5)

        // Then
        assertThat(result).isEmpty()
    }
}
