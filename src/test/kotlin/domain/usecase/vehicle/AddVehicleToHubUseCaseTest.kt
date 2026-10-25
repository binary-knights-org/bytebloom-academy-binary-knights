package domain.usecase.vehicle

import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse
import domain.model.exception.DomainException
import domain.model.exception.EntityValidationException
import domain.model.exception.NoUpdateFieldsException
import domain.model.exception.ResourceNotFoundException
import domain.model.validation.ValidationResult
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import domain.validator.vehicle.UpdateVehicleValidator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AddVehicleToHubUseCaseTest {

    private val warehouseRepository = mockk<WarehouseRepository>(relaxed = true)
    private val vehicleRepository = mockk<VehicleRepository>(relaxed = true)
    private val validator = mockk<UpdateVehicleValidator>()

    private val useCase = AddVehicleToHubUseCase(
        warehouseRepository = warehouseRepository,
        vehicleRepository = vehicleRepository,
        validator = validator
    )

    @Test
    fun `should add vehicle to the hub successfully`() = runTest {
        // Given
        val hub = createWarehouse(id = "WH-1")
        val vehicle = createVehicle(id = "TRK-1", hub = createWarehouse(id = "WH-2"))
        givenWarehouse(hub)
        givenVehicle(vehicle)
        every { validator.validate(any()) } returns ValidationResult.Valid

        // When
        val result = useCase(hub.id, vehicle.id)

        // Then
        val updatedVehicle = vehicle.copy(currentHub = hub)
        assertTrue(result.isSuccess)
        assertEquals(hub, result.getOrThrow())
        assertEquals(listOf(updatedVehicle), hub.stationedVehicles)
        coVerify(exactly = 1) { vehicleRepository.update(updatedVehicle) }
    }

    @Test
    fun `should keep previously stationed vehicles when a new vehicle is added`() = runTest {
        // Given
        val hub = createWarehouse(id = "WH-1")
        val firstVehicle = createVehicle(id = "TRK-1", hub = hub)
        val secondVehicle = createVehicle(id = "TRK-2", hub = createWarehouse(id = "WH-2"))
        hub.addVehicle(firstVehicle)
        givenWarehouse(hub)
        givenVehicle(secondVehicle)
        every { validator.validate(any()) } returns ValidationResult.Valid

        // When
        useCase(hub.id, secondVehicle.id)

        // Then
        assertEquals(
            listOf(firstVehicle, secondVehicle.copy(currentHub = hub)),
            hub.stationedVehicles
        )
    }

    @Test
    fun `should fail with ResourceNotFound when warehouse does not exist`() = runTest {
        // Given
        givenVehicle(createVehicle(id = "TRK-1", hub = createWarehouse(id = "WH-2")))
        coEvery { warehouseRepository.getById("WH-404") } returns null

        // When
        val result = useCase("WH-404", "TRK-1")

        // Then
        assertIs<ResourceNotFoundException>(result.exceptionOrNull())
        coVerify(exactly = 0) { vehicleRepository.update(any()) }
    }

    @Test
    fun `should fail with ResourceNotFound when vehicle does not exist`() = runTest {
        // Given
        givenWarehouse(createWarehouse(id = "WH-1"))
        coEvery { vehicleRepository.getById("TRK-404") } returns null

        // When
        val result = useCase("WH-1", "TRK-404")

        // Then
        assertIs<ResourceNotFoundException>(result.exceptionOrNull())
        coVerify(exactly = 0) { vehicleRepository.update(any()) }
    }

    @Test
    fun `should fail with EntityValidationException and not update when validation fails`() = runTest {
        // Given
        val hub = createWarehouse(id = "WH-1")
        val vehicle = createVehicle(id = "TRK-1", hub = createWarehouse(id = "WH-2"))
        givenWarehouse(hub)
        givenVehicle(vehicle)
        every { validator.validate(any()) } returns
                ValidationResult.Invalid<DomainException>(listOf(NoUpdateFieldsException()))

        // When
        val result = useCase(hub.id, vehicle.id)

        // Then
        assertIs<EntityValidationException>(result.exceptionOrNull())
        assertTrue(hub.stationedVehicles.isEmpty())
        coVerify(exactly = 0) { vehicleRepository.update(any()) }
    }

    private fun givenWarehouse(warehouse: Warehouse) {
        coEvery { warehouseRepository.getById(warehouse.id) } returns warehouse
    }

    private fun givenVehicle(vehicle: Vehicle) {
        coEvery { vehicleRepository.getById(vehicle.id) } returns vehicle
    }

    private fun createWarehouse(id: String) = Warehouse(
        id = id,
        name = "Central Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )

    private fun createVehicle(id: String, hub: Warehouse) = Vehicle(
        id = id,
        maxCapacityKg = 5000.0,
        costPerKm = 3.0,
        currentHub = hub
    )
}
