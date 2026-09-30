package domain.usecase.routing

import com.google.common.truth.Truth.assertThat
import domain.algorithm.pathfinding.ShortestPathRouter
import domain.model.RegionalZone
import domain.model.Warehouse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FindFewestHopsRouteUseCaseTest {

    private var router: ShortestPathRouter  = mockk<ShortestPathRouter>()
    private var useCase: FindFewestHopsRouteUseCase = FindFewestHopsRouteUseCase(router)

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
    fun `invoke when route with fewest hops is found then returns list of warehouses`() = runTest {
        // Given
        val expectedHopsPath = listOf(originHub, directHub)
        coEvery { router.findShortestPath(originHub, directHub) } returns expectedHopsPath

        // When
        val result = useCase(originHub, directHub)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).containsExactlyElementsIn(expectedHopsPath).inOrder()
        coVerify(exactly = 1) { router.findShortestPath(originHub, directHub) }
    }

    @Test
    fun `invoke when no reachable path exists then returns null`() = runTest {
        // Given
        coEvery { router.findShortestPath(originHub, directHub) } returns null

        // When
        val result = useCase(originHub, directHub)

        // Then
        assertThat(result).isNull()
        coVerify(exactly = 1) { router.findShortestPath(originHub, directHub) }
    }

    @Test
    fun `invoke when returned path is empty then returns empty list`() = runTest {
        // Given
        coEvery { router.findShortestPath(originHub, directHub) } returns emptyList()

        // When
        val result = useCase(originHub, directHub)

        // Then
        assertThat(result).isEmpty()
        coVerify(exactly = 1) { router.findShortestPath(originHub, directHub) }
    }

    @Test
    fun `invoke when router throws exception then propagates exception`() = runTest {
        // Given
        val expectedException = RuntimeException("Router traversal failed")
        coEvery { router.findShortestPath(originHub, directHub) } throws expectedException

        // When / Then
        val thrown = assertThrows<RuntimeException> {
            runTest { useCase(originHub, directHub) }
        }
        assertThat(thrown).hasMessageThat().isEqualTo("Router traversal failed")
        coVerify(exactly = 1) { router.findShortestPath(originHub, directHub) }
    }
}
