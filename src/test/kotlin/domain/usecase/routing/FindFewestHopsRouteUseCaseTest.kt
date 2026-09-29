package domain.usecase.routing

import com.google.common.truth.Truth.assertThat
import domain.algorithm.pathfinding.ShortestPathRouter
import domain.model.RegionalZone
import domain.model.Warehouse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FindFewestHopsRouteUseCaseTest {

    private lateinit var router: ShortestPathRouter
    private lateinit var useCase: FindFewestHopsRouteUseCase

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

    @BeforeEach
    fun setUp() {
        router = mockk()
        useCase = FindFewestHopsRouteUseCase(router)
    }

    @Test
    fun `invoke when route with fewest hops is found then returns list of warehouses`() = runBlocking {
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
    fun `invoke when no reachable path exists then returns null`() = runBlocking {
        // Given
        coEvery { router.findShortestPath(originHub, directHub) } returns null

        // When
        val result = useCase(originHub, directHub)

        // Then
        assertThat(result).isNull()
        coVerify(exactly = 1) { router.findShortestPath(originHub, directHub) }
    }

    @Test
    fun `invoke when returned path is empty then returns empty list`() = runBlocking {
        // Given
        coEvery { router.findShortestPath(originHub, directHub) } returns emptyList()

        // When
        val result = useCase(originHub, directHub)

        // Then
        assertThat(result).isEmpty()
        coVerify(exactly = 1) { router.findShortestPath(originHub, directHub) }
    }

    @Test
    fun `invoke when router throws exception then propagates exception`() = runBlocking {
        // Given
        val expectedException = RuntimeException("Router traversal failed")
        coEvery { router.findShortestPath(originHub, directHub) } throws expectedException

        // When / Then
        val thrown = assertThrows<RuntimeException> {
            runBlocking { useCase(originHub, directHub) }
        }
        assertThat(thrown).hasMessageThat().isEqualTo("Router traversal failed")
        coVerify(exactly = 1) { router.findShortestPath(originHub, directHub) }
    }
}
