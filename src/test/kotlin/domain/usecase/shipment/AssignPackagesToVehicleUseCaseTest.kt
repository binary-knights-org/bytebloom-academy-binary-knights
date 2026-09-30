package domain.usecase.shipment
import com.google.common.truth.Truth.assertThat
import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse
import domain.usecase.vehicle.AssignPackagesToVehicleUseCase
import domain.usecase.vehicle.FindSuitableVehicleUseCase
import domain.model.assignment.PackageVehicleAssignment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class AssignPackagesToVehicleUseCaseTest {

    private val findPackagesForConsolidationUseCase = mockk<FindPackagesForConsolidationUseCase>()
    private val findSuitableVehicleUseCase = mockk<FindSuitableVehicleUseCase>()
    private val useCase = AssignPackagesToVehicleUseCase(findPackagesForConsolidationUseCase, findSuitableVehicleUseCase)

    private val origin = createWarehouse("WH-001")
    private val destination = createWarehouse("WH-002")
    private val package1 = createPackage("PKG-001", 10.0, origin, destination)
    private val package2 = createPackage("PKG-002", 20.0, origin, destination)
    private val packages = listOf(package1, package2)
    private val vehicle = createVehicle("TRK-001", origin)

    @Test
    fun `should assign each package group to a suitable vehicle`() = runTest {
        // Given
        coEvery { findPackagesForConsolidationUseCase() } returns listOf(packages)
        coEvery { findSuitableVehicleUseCase(packages) } returns vehicle

        // When
        val result = useCase()

        // Then
        assertThat(result).containsExactly(PackageVehicleAssignment(packages = packages, vehicle = vehicle))

        coVerify(exactly = 1) { findPackagesForConsolidationUseCase() }
        coVerify(exactly = 1) { findSuitableVehicleUseCase(packages) }
    }

    @Test
    fun `should skip package group when no suitable vehicle is available`() = runTest {
        // Given
        coEvery { findPackagesForConsolidationUseCase() } returns listOf(packages)
        coEvery { findSuitableVehicleUseCase(packages) } returns null

        // When
        val result = useCase()

        // Then
        assertThat(result).isEmpty()

        coVerify(exactly = 1) { findPackagesForConsolidationUseCase() }
        coVerify(exactly = 1) { findSuitableVehicleUseCase(packages) }
    }

    @Test
    fun `should return empty list when there are no consolidation groups`() = runTest {
        // Given
        coEvery { findPackagesForConsolidationUseCase() } returns emptyList()

        // When
        val result = useCase()

        // Then
        assertThat(result).isEmpty()

        coVerify(exactly = 1) { findPackagesForConsolidationUseCase() }
        coVerify(exactly = 0) { findSuitableVehicleUseCase(any()) }
    }

    private fun createWarehouse(id: String): Warehouse =
        Warehouse(id = id, name = "Warehouse $id", regionalZone = RegionalZone.CENTRAL, latitude = 31.9, longitude = 35.2)

    private fun createPackage(
        id: String,
        weight: Double,
        origin: Warehouse,
        destination: Warehouse
    ): Package =
        Package(
            id = id,
            weight = weight,
            priority = Priority.STANDARD,
            originHub = origin,
            destinationHub = destination
        )

    private fun createVehicle(
        id: String,
        currentHub: Warehouse
    ): Vehicle =
        Vehicle(
            id = id,
            maxCapacityKg = 100.0,
            costPerKm = 2.0,
            currentHub = currentHub
        )
}