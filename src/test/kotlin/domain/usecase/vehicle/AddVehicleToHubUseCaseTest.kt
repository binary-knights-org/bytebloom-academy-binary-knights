package domain.usecase.vehicle

import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AddVehicleToHubUseCaseTest {

    private val useCase = AddVehicleToHubUseCase()

    @Test
    fun `should add vehicle to the hub successfully`() {
        // Given
        val hub = createWarehouse()
        val vehicle = createVehicle(id = "TRK-1", hub = hub)

        // When
        useCase(hub, vehicle)

        // Then
        assertEquals(listOf(vehicle), hub.stationedVehicles)
    }

    @Test
    fun `should keep previously stationed vehicles when a new vehicle is added`() {
        // Given
        val hub = createWarehouse()
        val firstVehicle = createVehicle(id = "TRK-1", hub = hub)
        val secondVehicle = createVehicle(id = "TRK-2", hub = hub)

        useCase(hub, firstVehicle )

        // When
        useCase(hub, secondVehicle )

        // Then
        assertEquals(listOf(firstVehicle, secondVehicle),hub.stationedVehicles)
    }

    @Test
    fun `should throw IllegalStateException when adding vehicle with existing id`() {
        // Given
        val hub = createWarehouse()
        val firstVehicle = createVehicle(id = "TRK-1", hub = hub)
        val duplicateVehicle = createVehicle(id = "TRK-1", hub = hub)

        // When
        useCase(hub, firstVehicle)

        // Then
        assertFailsWith<IllegalStateException> {
            useCase(hub, duplicateVehicle)
        }
    }

    private fun createWarehouse() = Warehouse(
        id = "WH-1",
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