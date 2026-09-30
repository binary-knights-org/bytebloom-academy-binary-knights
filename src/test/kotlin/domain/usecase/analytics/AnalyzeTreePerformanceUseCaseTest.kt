package domain.usecase.analytics

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class AnalyzeTreePerformanceUseCaseTest {

    private val useCase = AnalyzeTreePerformanceUseCase()

    @Test
    fun `should analyze tree performance for requested package count`() {
        // Given
        val count = 10

        // When
        val result = useCase(count)

        // Then
        assertThat(result.totalCount).isEqualTo(count)
        assertThat(result.unbalancedTotalSteps).isGreaterThan(0L)
        assertThat(result.balancedTotalSteps).isGreaterThan(0L)
    }

    @Test
    fun `should return zero metrics when count is zero`() {
        // Given
        val count = 0

        // When
        val result = useCase(count)

        // Then
        assertThat(result.totalCount).isEqualTo(0)
        assertThat(result.unbalancedTotalSteps).isEqualTo(0L)
        assertThat(result.unbalancedAvgSteps).isEqualTo(0.0)
        assertThat(result.balancedTotalSteps).isEqualTo(0L)
        assertThat(result.balancedAvgSteps).isEqualTo(0.0)
    }
}
