package domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import domain.repository.RouteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class DeleteRouteUseCaseTest {

    private val routeRepository = mockk<RouteRepository>()
    private val useCase = DeleteRouteUseCase(routeRepository)

    @Test
    fun `should delete route successfully`() = runTest {
        // Given
        val routeId = "RT-001"
        coEvery { routeRepository.delete(routeId) } returns Unit

        // When
        val result = useCase(routeId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(Unit)

        coVerify(exactly = 1) { routeRepository.delete(routeId) }
    }

    @Test
    fun `should return repository exception when deletion fails`() = runTest {
        // Given
        val routeId = "RT-001"
        val exception = RuntimeException("Database error")

        coEvery { routeRepository.delete(routeId) } throws exception

        // When
        val result = useCase(routeId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)

        coVerify(exactly = 1) { routeRepository.delete(routeId) }
    }
}
