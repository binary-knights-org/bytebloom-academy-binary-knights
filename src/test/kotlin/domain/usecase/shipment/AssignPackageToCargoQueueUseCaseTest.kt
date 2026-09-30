package domain.usecase.shipment

import domain.model.Package
import domain.model.Priority
import domain.model.Warehouse
import domain.model.RegionalZone
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AssignPackageToCargoQueueUseCaseTest {

    private val useCase = AssignPackageToCargoQueueUseCase()

    private val origin = Warehouse(
        name = "Origin Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    )

    private val destination = Warehouse(
        name = "Destination Hub",
        regionalZone = RegionalZone.SOUTH,
        latitude = 1.0,
        longitude = 1.0
    )

    @Test
    fun `should add package and sort cargo queue`() = runTest {
        // Given
        val warehouse = spyk(origin)
         val pkg = Package(
            weight = 10.0,
            priority = Priority.STANDARD,
            originHub = origin,
            destinationHub = destination
        )
        // When
        useCase(warehouse, pkg)

        // Then
        assertEquals(1, warehouse.cargoQueue.size)
        assertEquals(pkg, warehouse.cargoQueue[0])

        verify { warehouse.sortCargoQueueByWeightDescending() }
    }
}