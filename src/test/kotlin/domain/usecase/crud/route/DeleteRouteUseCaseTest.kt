package domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import data.exception.NetworkUnavailableException
import domain.model.exception.DataUnavailableException
import domain.model.exception.OperationFailedException
import domain.repository.RouteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class DeleteRouteUseCaseTest {

    private var routeRepository: RouteRepository = mockk()
    private var useCase: DeleteRouteUseCase = DeleteRouteUseCase(routeRepository)

    @Test
    fun `invoke when route is successfully deleted then returns Result success Unit`() =
        runTest {
            // Given
            val routeId = "RT-001"
            coEvery { routeRepository.delete(routeId) } returns true

            // When
            val result = useCase(routeId)

            // Then
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(Unit)
            coVerify(exactly = 1) { routeRepository.delete(routeId) }
        }

    @Test
    fun `invoke when repo delete returns false then returns Result failure with OperationFailedException`() =
        runTest {
            // Given
            val routeId = "RT-001"
            coEvery { routeRepository.delete(routeId) } returns false

            // When
            val result = useCase(routeId)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(OperationFailedException::class.java)
            coVerify(exactly = 1) { routeRepository.delete(routeId) }
        }

    @Test
    fun `invoke when repo delete throws NetworkUnavailableException then returns DataUnavailableException`() =
        runTest {
            // Given
            val routeId = "RT-001"
            coEvery { routeRepository.delete(routeId) } throws NetworkUnavailableException("No connection")

            // When
            val result = useCase(routeId)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(DataUnavailableException::class.java)
            coVerify(exactly = 1) { routeRepository.delete(routeId) }
        }

    @Test
    fun `invoke when repo delete throws generic exception then returns Result failure with OperationFailedException`() =
        runTest {
            // Given
            val routeId = "RT-001"
            coEvery { routeRepository.delete(routeId) } throws RuntimeException("Foreign key error")

            // When
            val result = useCase(routeId)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(OperationFailedException::class.java)
            coVerify(exactly = 1) { routeRepository.delete(routeId) }
        }
}
