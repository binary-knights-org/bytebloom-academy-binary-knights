package domain.usecase.search


import com.google.common.truth.Truth.assertThat
import domain.repository.WarehouseRepository
import domain.service.SearchState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SearchRepositoryInteractionTest {

    private val warehouses = listOf(aWarehouse("WH-001", "Hub-001"))

    @Test
    fun `five keystrokes read from the repository only once`() = runTest {
        val repository = mockk<WarehouseRepository> { coEvery { getAll() } returns warehouses }
        val useCase = SearchWarehousesUseCase(repository)

        val states = useCase(
            flow {
                listOf("H", "Hu", "Hub", "Hub-", "Hub-0").forEach { query ->
                    emit(query)
                    delay(30)
                }
                delay(500)
            }
        ).toList()

        coVerify(exactly = 1) { repository.getAll() }
        assertThat(states.map { it::class }).containsExactly(
            SearchState.Loading::class,
            SearchState.Success::class
        ).inOrder()
    }

    @Test
    fun `the same query typed twice reads from the repository once`() = runTest {
        val repository = mockk<WarehouseRepository> { coEvery { getAll() } returns warehouses }
        val useCase = SearchWarehousesUseCase(repository)

        useCase(
            flow {
                emit("hub")
                delay(400)
                emit("hub")
                delay(400)
            }
        ).toList()

        coVerify(exactly = 1) { repository.getAll() }
    }

    @Test
    fun `a repository failure becomes a failure state and the next query still works`() = runTest {
        val repository = mockk<WarehouseRepository>()
        coEvery { repository.getAll() } throws IllegalStateException("db down") andThen warehouses
        val useCase = SearchWarehousesUseCase(repository)

        val states = useCase(
            flow {
                emit("first")
                delay(500)
                emit("hub")
                delay(500)
            }
        ).toList()

        assertThat(states).hasSize(4)
        assertThat((states[1] as SearchState.Failure).error)
            .hasMessageThat().isEqualTo("db down")
        assertThat((states[3] as SearchState.Success<*>).items).hasSize(1)
    }

    @Test
    fun `clearing the input before the debounce never touches the repository`() = runTest {
        val repository = mockk<WarehouseRepository> { coEvery { getAll() } returns warehouses }
        val useCase = SearchWarehousesUseCase(repository)

        val states = useCase(
            flow {
                emit("hub")
                delay(100)
                emit("")
                delay(500)
            }
        ).toList()

        coVerify(exactly = 0) { repository.getAll() }
        assertThat(states).containsExactly(SearchState.Idle)
    }
}
