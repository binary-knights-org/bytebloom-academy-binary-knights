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

class FindFewestHopsRouteUseCaseTest {

    private val router = mockk<ShortestPathRouter>()
    private val useCase = FindFewestHopsRouteUseCase(router)

    private val originHub = Warehouse(
        id = "WH-ORIGIN",
        name = "Origin Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 10.0,
        longitude = 20.0
    )

    private val directHub = Warehouse(
        id = "WH-DIRECT",
        name = "Direct Destination Hub",
        regionalZone = RegionalZone.SOUTH,
        latitude = 30.0,
        longitude = 40.0
    )

    @Test
    fun `should return route with fewest hops`() = runTest {
        val expectedPath = listOf(originHub, directHub)
        coEvery {
            router.findShortestPath(originHub, directHub)
        } returns expectedPath

        val result = useCase(originHub, directHub)

        assertThat(result).containsExactlyElementsIn(expectedPath).inOrder()
    }

    @Test
    fun `should return null when no reachable route exists`() = runTest {
        coEvery {
            router.findShortestPath(originHub, directHub)
        } returns null

        val result = useCase(originHub, directHub)

        assertThat(result).isNull()
    }

    @Test
    fun `should return empty list when router returns empty path`() = runTest {
        coEvery {
            router.findShortestPath(originHub, directHub)
        } returns emptyList()

        val result = useCase(originHub, directHub)

        assertThat(result).isEmpty()
    }

    @Test
    fun `should propagate router exception`() = runTest {
        coEvery {
            router.findShortestPath(originHub, directHub)
        } throws RuntimeException("Router traversal failed")

        assertThrows<RuntimeException> {
            useCase(originHub, directHub)
        }
    }
}
