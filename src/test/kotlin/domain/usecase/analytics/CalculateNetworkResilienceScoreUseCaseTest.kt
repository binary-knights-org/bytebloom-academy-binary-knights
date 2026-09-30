package domain.usecase.analytics


import com.google.common.truth.Truth.assertThat
import domain.model.RegionalZone
import domain.model.Route
import domain.model.Warehouse
import org.junit.jupiter.api.Test

class CalculateNetworkResilienceScoreUseCaseTest {

    private val useCase = CalculateNetworkResilienceScoreUseCase()

    private val warehouse1 = Warehouse(
        id = "WH-001",
        name = "Hub 1",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    )

    private val warehouse2 = Warehouse(
        id = "WH-002",
        name = "Hub 2",
        regionalZone = RegionalZone.SOUTH,
        latitude = 1.0,
        longitude = 1.0
    )

    private val warehouse3 = Warehouse(
        id = "WH-003",
        name = "Hub 3",
        regionalZone = RegionalZone.EAST,
        latitude = 2.0,
        longitude = 2.0
    )

    private val route = Route(
        distanceKm = 50.0,
        typicalDelayMin = 10,
        originHub = warehouse1,
        destinationHub = warehouse2
    )

    @Test
    fun `should return perfect score for network with one warehouse`() {
        // Given
        val warehouses = listOf(warehouse1)

        // When
        val result = useCase(warehouses)

        // Then
        assertThat(result).isEqualTo(100.0)
    }

    @Test
    fun `should return perfect score when all warehouses remain connected after any removal`() {
        // Given
        warehouse1.addRoute(route)
        val warehouses = listOf(warehouse1, warehouse2)

        // When
        val result = useCase(warehouses)

        // Then
        assertThat(result).isEqualTo(100.0)
    }

    @Test
    fun `should return zero score when network becomes disconnected`() {
        // Given
        val warehouses = listOf(warehouse1, warehouse2, warehouse3)

        // When
        val result = useCase(warehouses)

        // Then
        assertThat(result).isEqualTo(0.0)
    }
}