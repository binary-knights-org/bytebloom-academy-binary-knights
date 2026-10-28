package domain.usecase.search


import com.google.common.truth.Truth.assertThat
import domain.repository.WarehouseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SearchWarehousesUseCaseTest {

    private val warehouses = listOf(
        aWarehouse("WH-001", "Hub-001", "CENTRAL"),
        aWarehouse("WH-002", "Hub-002", "WEST"),
        aWarehouse("WH-004", "Hub-004", "CENTRAL"),
        aWarehouse("WH-073", "HUB-073", "SOUTH")
    )

    private val useCase = SearchWarehousesUseCase(
        mockk<WarehouseRepository> { coEvery { getAll() } returns warehouses }
    )

    @Test
    fun `finds a warehouse by id ignoring case`() = runTest {
        assertThat(useCase.searchFor("wh-073").map { it.id })
            .containsExactly("WH-073")
    }

    @Test
    fun `finds a warehouse by name`() = runTest {
        assertThat(useCase.searchFor("hub-002").map { it.id })
            .containsExactly("WH-002")
    }

    @Test
    fun `finds warehouses by regional zone`() = runTest {
        assertThat(useCase.searchFor("central").map { it.id })
            .containsExactly("WH-001", "WH-004")
            .inOrder()
    }

    @Test
    fun `matches partially`() = runTest {
        assertThat(useCase.searchFor("hub-00").map { it.id })
            .containsExactly("WH-001", "WH-002", "WH-004")
            .inOrder()
    }

    @Test
    fun `ignores surrounding spaces`() = runTest {
        assertThat(useCase.searchFor("  central  ").map { it.id })
            .containsExactly("WH-001", "WH-004")
            .inOrder()
    }

    @Test
    fun `returns an empty list when nothing matches`() = runTest {
        assertThat(useCase.searchFor("zzz")).isEmpty()
    }
}
