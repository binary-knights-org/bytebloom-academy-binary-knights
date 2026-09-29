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

class FindOptimalPathUseCaseTest {

    private lateinit var router: ShortestPathRouter
    private lateinit var useCase: FindOptimalPathUseCase

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

    @BeforeEach
    fun setUp() {
        router = mockk()
        useCase = FindOptimalPathUseCase(router)
    }

    @Test
    fun `invoke when path exists then returns list of warehouses along optimal route`() = runBlocking {
        // Given
        val expectedPath = listOf(originHub, intermediateHub, destinationHub)
        coEvery { router.findShortestPath(originHub, destinationHub) } returns expectedPath

        // When
        val result = useCase(originHub, destinationHub)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).containsExactlyElementsIn(expectedPath).inOrder()
        coVerify(exactly = 1) { router.findShortestPath(originHub, destinationHub) }
    }

    @Test
    fun `invoke when no path exists between hubs then returns null`() = runBlocking {
        // Given
        coEvery { router.findShortestPath(originHub, destinationHub) } returns null

        // When
        val result = useCase(originHub, destinationHub)

        // Then
        assertThat(result).isNull()
        coVerify(exactly = 1) { router.findShortestPath(originHub, destinationHub) }
    }

    @Test
    fun `invoke when path is empty then returns empty list`() = runBlocking {
        // Given
        coEvery { router.findShortestPath(originHub, destinationHub) } returns emptyList()

        // When
        val result = useCase(originHub, destinationHub)

        // Then
        assertThat(result).isEmpty()
        coVerify(exactly = 1) { router.findShortestPath(originHub, destinationHub) }
    }

    @Test
    fun `invoke when router throws exception then exception is propagated`() = runBlocking {
        // Given
        val expectedException = IllegalStateException("Graph corrupted")
        coEvery { router.findShortestPath(originHub, destinationHub) } throws expectedException

        // When / Then
        val thrown = assertThrows<IllegalStateException> {
            runBlocking { useCase(originHub, destinationHub) }
        }
        assertThat(thrown).hasMessageThat().isEqualTo("Graph corrupted")
        coVerify(exactly = 1) { router.findShortestPath(originHub, destinationHub) }
    }
}
