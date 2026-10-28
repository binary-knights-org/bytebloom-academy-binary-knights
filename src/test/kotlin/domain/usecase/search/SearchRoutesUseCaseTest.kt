package domain.usecase.search


import com.google.common.truth.Truth.assertThat
import domain.repository.RouteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SearchRoutesUseCaseTest {

    private val hub1 = aWarehouse("WH-001", "Hub-001")
    private val hub2 = aWarehouse("WH-002", "Hub-002")
    private val hub3 = aWarehouse("WH-003", "Hub-003")
    private val hub4 = aWarehouse("WH-004", "Hub-004")

    private val routes = listOf(
        aRoute("RT-00001", origin = hub1, destination = hub2),
        aRoute("RT-00002", origin = hub2, destination = hub3),
        aRoute("RT-00003", origin = hub3, destination = hub4)
    )

    private val useCase = SearchRoutesUseCase(
        mockk<RouteRepository> { coEvery { getAll() } returns routes }
    )

    @Test
    fun `finds a route by id ignoring case`() = runTest {
        assertThat(useCase.searchFor("rt-00002").map { it.id })
            .containsExactly("RT-00002")
    }

    @Test
    fun `finds routes by origin or destination hub id`() = runTest {
        assertThat(useCase.searchFor("WH-003").map { it.id })
            .containsExactly("RT-00002", "RT-00003")
            .inOrder()
    }

    @Test
    fun `finds routes by hub name`() = runTest {
        assertThat(useCase.searchFor("hub-001").map { it.id })
            .containsExactly("RT-00001")
    }

    @Test
    fun `returns every route for a shared prefix`() = runTest {
        assertThat(useCase.searchFor("RT-0000")).hasSize(3)
    }

    @Test
    fun `returns an empty list when nothing matches`() = runTest {
        assertThat(useCase.searchFor("zzz")).isEmpty()
    }
}
