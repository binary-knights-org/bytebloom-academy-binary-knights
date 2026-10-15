package domain.usecase.vehicle

import com.google.common.truth.Truth.assertThat
import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class DispatchVehicleUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val warehouseRepository = mockk<WarehouseRepository>()

    private val useCase = DispatchVehicleUseCase(
        vehicleRepository,
        warehouseRepository
    )

    private val origin = Warehouse(
        id = "WH-ORIGIN",
        name = "Origin",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    )

    private val destination = Warehouse(
        id = "WH-DESTINATION",
        name = "Destination",
        regionalZone = RegionalZone.SOUTH,
        latitude = 1.0,
        longitude = 1.0
    )

    private val warehouse = Warehouse(
        id = "WH-1",
        name = "Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    )

    private val vehicle = Vehicle(
        id = "TRK-1",
        maxCapacityKg = 50.0,
        costPerKm = 1.0,
        currentHub = warehouse
    )

    private fun createPackage(
        id: String,
        weight: Double
    ) = Package(
        id = id,
        weight = weight,
        priority = Priority.STANDARD,
        originHub = origin,
        destinationHub = destination
    )

    private fun mockRepositories(
        warehouses: List<Warehouse> = listOf(warehouse),
        vehicles: List<Vehicle> = listOf(vehicle)
    ) {
        coEvery { warehouseRepository.getAll() } returns warehouses
        coEvery { vehicleRepository.getAll() } returns vehicles
    }

    @Test
    fun `should return package when dispatch succeeds`() = runTest {
        val pkg = createPackage("PKG-1", 10.0)
        warehouse.addPackage(pkg)
        mockRepositories()

        val result = useCase(vehicle, warehouse)

        assertThat(result).containsExactly(pkg)
    }

    @Test
    fun `should add dispatched package to vehicle cargo`() = runTest {
        val pkg = createPackage("PKG-1", 10.0)
        warehouse.addPackage(pkg)
        mockRepositories()

        useCase(vehicle, warehouse)

        assertThat(vehicle.loadedCargo).containsExactly(pkg)
    }

    @Test
    fun `should update vehicle load after dispatch`() = runTest {
        val pkg = createPackage("PKG-1", 10.0)
        warehouse.addPackage(pkg)
        mockRepositories()

        useCase(vehicle, warehouse)

        assertThat(vehicle.currentLoadKg).isEqualTo(10.0)
    }

    @Test
    fun `should remove dispatched package from warehouse queue`() = runTest {
        val pkg = createPackage("PKG-1", 10.0)
        warehouse.addPackage(pkg)
        mockRepositories()

        useCase(vehicle, warehouse)

        assertThat(warehouse.cargoQueue).isEmpty()
    }

    @Test
    fun `should not exceed vehicle weight capacity`() = runTest {
        val firstPackage = createPackage("PKG-1", 30.0)
        val secondPackage = createPackage("PKG-2", 25.0)

        warehouse.addPackage(firstPackage)
        warehouse.addPackage(secondPackage)
        mockRepositories()

        val result = useCase(vehicle, warehouse)

        assertThat(result).containsExactly(firstPackage)
    }

    @Test
    fun `should keep package in queue when vehicle capacity is insufficient`() = runTest {
        val firstPackage = createPackage("PKG-1", 30.0)
        val secondPackage = createPackage("PKG-2", 25.0)

        warehouse.addPackage(firstPackage)
        warehouse.addPackage(secondPackage)
        mockRepositories()

        useCase(vehicle, warehouse)

        assertThat(warehouse.cargoQueue).containsExactly(secondPackage)
    }

    @Test
    fun `should return empty list when warehouse does not exist`() = runTest {
        mockRepositories(warehouses = emptyList())

        val result = useCase(vehicle, warehouse)

        assertThat(result).isEmpty()
    }

    @Test
    fun `should return empty list when vehicle does not exist`() = runTest {
        val pkg = createPackage("PKG-1", 10.0)
        warehouse.addPackage(pkg)
        mockRepositories(vehicles = emptyList())

        val result = useCase(vehicle, warehouse)

        assertThat(result).isEmpty()
    }

    @Test
    fun `should preserve queued packages when vehicle does not exist`() = runTest {
        val pkg = createPackage("PKG-1", 10.0)
        warehouse.addPackage(pkg)
        mockRepositories(vehicles = emptyList())

        useCase(vehicle, warehouse)

        assertThat(warehouse.cargoQueue).containsExactly(pkg)
    }

    @Test
    fun `should not dispatch when vehicle belongs to another warehouse`() = runTest {
        val otherWarehouse = Warehouse(
            id = "WH-OTHER",
            name = "Other Hub",
            regionalZone = RegionalZone.SOUTH,
            latitude = 2.0,
            longitude = 2.0
        )

        val misplacedVehicle = Vehicle(
            id = "TRK-1",
            maxCapacityKg = 50.0,
            costPerKm = 1.0,
            currentHub = otherWarehouse
        )

        val pkg = createPackage("PKG-1", 10.0)
        warehouse.addPackage(pkg)
        mockRepositories(vehicles = listOf(misplacedVehicle))

        val result = useCase(misplacedVehicle, warehouse)

        assertThat(result).isEmpty()
    }

    @Test
    fun `should preserve queue when vehicle belongs to another warehouse`() = runTest {
        val otherWarehouse = Warehouse(
            id = "WH-OTHER",
            name = "Other Hub",
            regionalZone = RegionalZone.SOUTH,
            latitude = 2.0,
            longitude = 2.0
        )

        val misplacedVehicle = Vehicle(
            id = "TRK-1",
            maxCapacityKg = 50.0,
            costPerKm = 1.0,
            currentHub = otherWarehouse
        )

        val pkg = createPackage("PKG-1", 10.0)
        warehouse.addPackage(pkg)
        mockRepositories(vehicles = listOf(misplacedVehicle))

        useCase(misplacedVehicle, warehouse)

        assertThat(warehouse.cargoQueue).containsExactly(pkg)
    }

    @Test
    fun `should propagate warehouse repository exception`() = runTest {
        coEvery {
            warehouseRepository.getAll()
        } throws IllegalStateException("Failed to get warehouses")

        val result = runCatching {
            useCase(vehicle, warehouse)
        }

        assertThat(result.exceptionOrNull()?.message)
            .isEqualTo("Failed to get warehouses")
    }
}
