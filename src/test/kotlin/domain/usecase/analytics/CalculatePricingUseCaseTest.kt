package domain.usecase.analytics

import com.google.common.truth.Truth.assertThat
import domain.model.Package
import domain.model.Priority
import domain.model.Route
import domain.model.Warehouse
import domain.model.RegionalZone
import domain.model.component.PackageComponent
import domain.model.assignment.PricingRequest
import domain.pricing.DispatchStrategy
import domain.pricing.RoutePricingEngine
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions

class CalculatePricingUseCaseTest {

    private val pricingEngine = mockk<RoutePricingEngine> {
        every { calculateCost(any(), any()) } returns 200.0
        every { setStrategy(any()) } returns Unit
    }

    private val useCase = CalculatePricingUseCase(pricingEngine)

    private val origin = Warehouse(
        name = "Origin",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    )

    private val destination = Warehouse(
        name = "Destination",
        regionalZone = RegionalZone.SOUTH,
        latitude = 1.0,
        longitude = 1.0
    )

    private val package1 = Package(
        weight = 10.0,
        priority = Priority.URGENT,
        originHub = origin,
        destinationHub = destination
    )

    private val route = Route(
        distanceKm = 100.0,
        typicalDelayMin = 10,
        originHub = origin,
        destinationHub = destination
    )

    private val strategy = mockk<DispatchStrategy>()

    private val component = mockk<PackageComponent> {
        every { calculateTransitRate(any()) } returns 50.0
    }

    @Test
    fun `should calculate final package price based on component transit rate`() {
        // Given
        val request = PricingRequest(package1, component, route, strategy)

        // When
        val result = useCase(request)

        // Then
        assertThat(result).isEqualTo(50.0)
    }

    @Test
    fun `should propagate exception when calculating route cost fails`() {
        // Given
        val expectedException = IllegalStateException("Failed to calculate route cost")

        every { pricingEngine.calculateCost(any(), any()) } throws expectedException

        val request = PricingRequest(package1, component, route, strategy)

        // When
        val thrown = Assertions.assertThrows(IllegalStateException::class.java
        ) {
            useCase(request)
        }

        // Then
        assertThat(thrown.message)
            .isEqualTo("Failed to calculate route cost")
    }
}
