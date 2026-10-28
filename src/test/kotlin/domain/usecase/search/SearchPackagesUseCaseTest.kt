package domain.usecase.search

import com.google.common.truth.Truth.assertThat
import domain.repository.PackageRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SearchPackagesUseCaseTest {

    private val hub073 = aWarehouse("WH-073", "Hub-073")
    private val hub037 = aWarehouse("WH-037", "Hub-037")
    private val hub028 = aWarehouse("WH-028", "Hub-028")
    private val hub095 = aWarehouse("WH-095", "Hub-095")
    private val hub017 = aWarehouse("WH-017", "Hub-017")

    private val packages = listOf(
        aPackage("PKG-000001", origin = hub073, destination = hub037, priority = "STANDARD"),
        aPackage("PKG-000004", origin = hub095, destination = hub017, priority = "LOW"),
        aPackage("PKG-000005", origin = hub028, destination = hub073, priority = "URGENT")
    )

    private val useCase = SearchPackagesUseCase(
        mockk<PackageRepository> { coEvery { getAll() } returns packages }
    )

    @Test
    fun `finds a package by id ignoring case`() = runTest {
        assertThat(useCase.searchFor("pkg-000004").map { it.id })
            .containsExactly("PKG-000004")
    }

    @Test
    fun `finds packages by origin or destination hub id`() = runTest {
        assertThat(useCase.searchFor("WH-073").map { it.id })
            .containsExactly("PKG-000001", "PKG-000005")
            .inOrder()
    }

    @Test
    fun `finds packages by hub name`() = runTest {
        assertThat(useCase.searchFor("hub-037").map { it.id })
            .containsExactly("PKG-000001")
    }

    @Test
    fun `finds packages by priority`() = runTest {
        assertThat(useCase.searchFor("urgent")
            .map { it.id }).containsExactly("PKG-000005")
    }

    @Test
    fun `returns an empty list when nothing matches`() = runTest {
        assertThat(useCase.searchFor("WH-999")).isEmpty()
    }
}
