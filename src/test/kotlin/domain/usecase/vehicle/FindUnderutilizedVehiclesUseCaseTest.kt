package domain.usecase.vehicle

import com.google.common.truth.Truth.assertThat
import domain.model.Package
import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import kotlin.test.Test
import domain.model.Priority

class FindUnderutilizedVehiclesUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val useCase = FindUnderutilizedVehiclesUseCase(vehicleRepository)

    private val warehouse = Warehouse(
        name = "Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    )

    private val destination = Warehouse(
        name = "Destination",
        regionalZone = RegionalZone.SOUTH,
        latitude = 1.0,
        longitude = 1.0
    )

    private val vehicle = Vehicle(
        maxCapacityKg = 100.0,
        costPerKm = 1.0,
        currentHub = warehouse
    )

    private val package1 = Package(
        weight = 20.0,
        priority = Priority.STANDARD,
        originHub = warehouse,
        destinationHub = destination
    )

    @Test
    fun `should return underutilized vehicles`() = runTest {
        // Given
        warehouse.addPackage(package1)

        coEvery { vehicleRepository.getAll() } returns listOf(vehicle)

        // When
        val result = useCase(0.5)

        // Then
        assertThat(result).containsExactly(vehicle)
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

    @Test
    fun `should propagate exception when getting vehicles fails`() = runTest {
        // Given
        val expectedException = IllegalStateException("Failed to get vehicles")

        coEvery { vehicleRepository.getAll() } throws expectedException

        // When
        val thrown = Assertions.assertThrows(IllegalStateException::class.java
        ) {
            runBlocking {
                useCase(0.5)
            }
        }

        // Then
        assertThat(thrown.message)
            .isEqualTo("Failed to get vehicles")
    }
}
