package domain.usecase.routing

import com.google.common.truth.Truth.assertThat
import domain.model.Warehouse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CompareRoutingStrategiesUseCaseTest {

    private val fewestHopsUseCase = mockk<FindFewestHopsRouteUseCase>()
    private val optimalPathUseCase = mockk<FindOptimalPathUseCase>()
    private val bidirectionalUseCase = mockk<FindBidirectionalRouteUseCase>()
    private val useCase = CompareRoutingStrategiesUseCase(
        fewestHopsUseCase,
        optimalPathUseCase,
        bidirectionalUseCase
    )

    @Test
    fun `should compare all routing strategies`() = runTest{

        // Given
        val origin = mockk<Warehouse>()
        val destination = mockk<Warehouse>()

        val fewestHopsResult = listOf(origin, destination)
        val optimalResult = listOf(origin, destination)
        val bidirectionalResult = listOf(origin, destination)

        coEvery { fewestHopsUseCase(origin, destination) } returns fewestHopsResult
        coEvery { optimalPathUseCase(origin, destination) } returns optimalResult
        coEvery { bidirectionalUseCase(origin, destination) } returns bidirectionalResult

        // When
        val result = useCase(origin, destination)

        // Then
        assertThat(result.fewestHops).isEqualTo(fewestHopsResult)
        assertThat(result.optimalDistance).isEqualTo(optimalResult)
        assertThat(result.bidirectional).isEqualTo(bidirectionalResult)

        coVerify(exactly = 1) { fewestHopsUseCase(origin, destination) }
        coVerify(exactly = 1) { optimalPathUseCase(origin, destination) }
        coVerify(exactly = 1) { bidirectionalUseCase(origin, destination) }
    }
}

