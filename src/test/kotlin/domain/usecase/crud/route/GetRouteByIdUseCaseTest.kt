package domain.usecase.crud.route

import com.google.common.truth.Truth.assertThat
import data.exception.NetworkUnavailableException
import domain.model.RegionalZone
import domain.model.Route
import domain.model.Warehouse
import domain.model.exception.DataUnavailableException
import domain.model.exception.OperationFailedException
import domain.model.exception.ResourceNotFoundException
import domain.repository.RouteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class GetRouteByIdUseCaseTest {

    private var routeRepository: RouteRepository = mockk()
    private var useCase: GetRouteByIdUseCase = GetRouteByIdUseCase(routeRepository)

    private val originHub = Warehouse(
        id = "WH-ORIGIN",
        name = "Origin Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 10.0,
        longitude = 20.0
    )

    private val destinationHub = Warehouse(
        id = "WH-DEST",
        name = "Destination Hub",
        regionalZone = RegionalZone.SOUTH,
        latitude = 30.0,
        longitude = 40.0
    )

    private val existingRoute = Route(
        id = "RT-001",
        distanceKm = 100.0,
        typicalDelayMin = 10,
        originHub = originHub,
        destinationHub = destinationHub
    )

    @Test
    fun `invoke when route exists then returns Result success with route`() =
        runTest {
            // Given
            val routeId = "RT-001"
            coEvery { routeRepository.getById(routeId) } returns existingRoute

            // When
            val result = useCase(routeId)

            // Then
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(existingRoute)
            coVerify(exactly = 1) { routeRepository.getById(routeId) }
        }

    @Test
    fun `invoke when route does not exist then returns Result failure with ResourceNotFoundException`() =
        runTest {
            // Given
            val routeId = "RT-NON-EXISTENT"
            coEvery { routeRepository.getById(routeId) } returns null

            // When
            val result = useCase(routeId)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(ResourceNotFoundException::class.java)
            coVerify(exactly = 1) { routeRepository.getById(routeId) }
        }

    @Test
    fun `invoke when repo throws NetworkUnavailableException then returns DataUnavailableException`() =
        runTest {
            // Given
            val routeId = "RT-001"
            coEvery { routeRepository.getById(routeId) } throws NetworkUnavailableException("No network")

            // When
            val result = useCase(routeId)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(DataUnavailableException::class.java)
            coVerify(exactly = 1) { routeRepository.getById(routeId) }
        }

    @Test
    fun `invoke when repo throws generic exception then returns Result failure with OperationFailedException`() =
        runTest {
            // Given
            val routeId = "RT-001"
            coEvery { routeRepository.getById(routeId) } throws RuntimeException("Database timeout")

            // When
            val result = useCase(routeId)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(OperationFailedException::class.java)
            coVerify(exactly = 1) { routeRepository.getById(routeId) }
        }
}
