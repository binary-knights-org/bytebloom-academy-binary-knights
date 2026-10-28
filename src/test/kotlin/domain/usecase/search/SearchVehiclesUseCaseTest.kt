package domain.usecase.search


import com.google.common.truth.Truth.assertThat
import domain.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SearchVehiclesUseCaseTest {

    private val hub1 = aWarehouse("WH-001", "Hub-001")
    private val hub73 = aWarehouse("WH-073", "Hub-073")

    private val vehicles = listOf(
        aVehicle("VEH-001", hub = hub1),
        aVehicle("VEH-002", hub = hub73)
    )

    private val useCase = SearchVehiclesUseCase(
        mockk<VehicleRepository> { coEvery { getAll() } returns vehicles }
    )

    @Test
    fun `finds a vehicle by id ignoring case`() = runTest {
        assertThat(useCase.searchFor("veh-002").map { it.id })
            .containsExactly("VEH-002")
    }

    @Test
    fun `finds vehicles by current hub id`() = runTest {
        assertThat(useCase.searchFor("WH-073").map { it.id })
            .containsExactly("VEH-002")
    }

    @Test
    fun `finds vehicles by current hub name`() = runTest {
        assertThat(useCase.searchFor("hub-001").map { it.id })
            .containsExactly("VEH-001")
    }

    @Test
    fun `returns an empty list when nothing matches`() = runTest {
        assertThat(useCase.searchFor("zzz")).isEmpty()
    }
}
