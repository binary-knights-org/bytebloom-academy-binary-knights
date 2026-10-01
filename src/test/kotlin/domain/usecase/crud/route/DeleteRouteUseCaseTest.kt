package domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import domain.model.exception.OperationFailedException
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
    fun `invoke when route is successfully deleted then returns Result success Unit`() = runTest {
        // Given
        val routeId = "RT-001"

        coEvery {
            routeRepository.delete(routeId)
        } returns true

        // When
        val result = useCase(routeId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(Unit)

        coVerify(exactly = 1) {
            routeRepository.delete(routeId)
        }
    }

    @Test
    fun `invoke when repo delete returns false then returns Result failure with OperationFailedException`() = runTest {
        // Given
        val routeId = "RT-001"

        coEvery {
            routeRepository.delete(routeId)
        } returns false

        // When
        val result = useCase(routeId)

        // Then
        assertThat(result.isFailure).isTrue()

        assertThat(result.exceptionOrNull() is OperationFailedException)

        coVerify(exactly = 1) {
            routeRepository.delete(routeId)
        }
    }

    @Test
    fun `invoke when repo delete throws exception then returns the same exception`() = runTest {
        // Given
        val routeId = "RT-001"
        val exception = RuntimeException("Database error")

        coEvery {
            routeRepository.delete(routeId)
        } throws exception

        // When
        val result = useCase(routeId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)

        coVerify(exactly = 1) {
            routeRepository.delete(routeId)
        }
    }
}
