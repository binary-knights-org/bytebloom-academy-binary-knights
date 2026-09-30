package domain.usecase.warehouse

import domain.algorithm.tree.HubNode
import domain.model.RegionalZone
import domain.model.Warehouse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TraceHubLineageUseCaseTest {

    private val useCase = TraceHubLineageUseCase()

    private val globalWarehouse = Warehouse(
        id = "WH-001",
        name = "Hub-001",
        regionalZone = RegionalZone.NORTH,
        latitude = 37.91,
        longitude = -88.46
    )

    private val regionalWarehouse = Warehouse(
        id = "WH-002",
        name = "Hub-002",
        regionalZone = RegionalZone.CENTRAL,
        latitude = 32.07,
        longitude = -116.21
    )

    private val localWarehouse = Warehouse(
        id = "WH-003",
        name = "Hub-003",
        regionalZone = RegionalZone.SOUTH,
        latitude = 29.64,
        longitude = -99.44
    )

    @Test
    fun `should return only global hub when node is global hub`() {
        // Given
        val globalHub = HubNode.GlobalHub(globalWarehouse)

        // When
        val lineage = useCase(globalHub)

        // Then
        assertEquals(
            listOf(globalHub),
            lineage
        )
    }

    @Test
    fun `should return regional center followed by global hub`() {
        // Given
        val globalHub = HubNode.GlobalHub(globalWarehouse)

        val regionalCenter = HubNode.RegionalCenter(
            warehouse = regionalWarehouse,
            parent = globalHub
        )

        // When
        val lineage = useCase(regionalCenter)

        // Then
        assertEquals(
            listOf(regionalCenter, globalHub),
            lineage
        )
    }

    @Test
    fun `should return local depot followed by regional center and global hub`() {
        // Given
        val globalHub = HubNode.GlobalHub(globalWarehouse)

        val regionalCenter = HubNode.RegionalCenter(
            warehouse = regionalWarehouse,
            parent = globalHub
        )

        val localDepot = HubNode.LocalDepot(
            warehouse = localWarehouse,
            parent = regionalCenter
        )

        // When
        val lineage = useCase(localDepot)

        // Then
        assertEquals(
            listOf(
                localDepot,
                regionalCenter,
                globalHub
            ),
            lineage
        )
    }
}
