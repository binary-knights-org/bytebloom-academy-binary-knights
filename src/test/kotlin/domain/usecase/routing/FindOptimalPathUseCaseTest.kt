package domain.usecase.routing

import com.google.common.truth.Truth.assertThat
import domain.algorithm.pathfinding.ShortestPathRouter
import domain.model.RegionalZone
import domain.model.Warehouse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FindOptimalPathUseCaseTest {

    private val router = mockk<ShortestPathRouter>()
    private val useCase = FindOptimalPathUseCase(router)

    private val originHub = Warehouse(
        id = "WH-ORIGIN",
        name = "Origin Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 10.0,
        longitude = 20.0
    )

    private val intermediateHub = Warehouse(
        id = "WH-INTERMEDIATE",
        name = "Intermediate Hub",
        regionalZone = RegionalZone.CENTRAL,
        latitude = 15.0,
        longitude = 25.0
    )

    private val destinationHub = Warehouse(
        id = "WH-DEST",
        name = "Destination Hub",
        regionalZone = RegionalZone.SOUTH,
        latitude = 30.0,
        longitude = 40.0
    )

    @Test
    fun `should return optimal path when path exists`() = runTest {
        val expectedPath = listOf(
            originHub,
            intermediateHub,
            destinationHub
        )
        coEvery {
            router.findShortestPath(originHub, destinationHub)
        } returns expectedPath

        val result = useCase(originHub, destinationHub)

        assertThat(result).containsExactlyElementsIn(expectedPath).inOrder()
    }

    @Test
    fun `should return null when no path exists`() = runTest {
        coEvery {
            router.findShortestPath(originHub, destinationHub)
        } returns null

        val result = useCase(originHub, destinationHub)

        assertThat(result).isNull()
    }

    @Test
    fun `should return empty list when router returns empty path`() = runTest {
        coEvery {
            router.findShortestPath(originHub, destinationHub)
        } returns emptyList()

        val result = useCase(originHub, destinationHub)

        assertThat(result).isEmpty()
    }

    @Test
    fun `should propagate router exception`() = runTest {
        coEvery {
            router.findShortestPath(originHub, destinationHub)
        } throws IllegalStateException("Graph corrupted")

        assertThrows<IllegalStateException> {
            useCase(originHub, destinationHub)
        }
    }
}
