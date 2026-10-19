package domain.usecase.analytics

import com.google.common.truth.Truth.assertThat
import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Route
import domain.model.Warehouse
import domain.model.assignment.PricingRequest
import domain.model.component.PackageComponent
import domain.pricing.DispatchStrategy
import domain.pricing.RoutePricingEngine
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CalculatePricingUseCaseTest {

    private val pricingEngine = mockk<RoutePricingEngine>(relaxed = true) {
        every { calculateCost(any(), any()) } returns 200.0
    }

    private val useCase = CalculatePricingUseCase(pricingEngine)

    private val origin = Warehouse(
        id = "WH-1",
        name = "Origin",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    )

    private val destination = Warehouse(
        id = "WH-2",
        name = "Destination",
        regionalZone = RegionalZone.SOUTH,
        latitude = 1.0,
        longitude = 1.0
    )

    private val package1 = Package(
        id = "PKG-1",
        weight = 10.0,
        priority = Priority.URGENT,
        originHub = origin,
        destinationHub = destination
    )

    private val route = Route(
        id = "RTE-1",
        distanceKm = 100.0,
        typicalDelayMin = 10,
        originHub = origin,
        destinationHub = destination
    )

    private val strategy = mockk<DispatchStrategy>(relaxed = true)

    private val component = mockk<PackageComponent>(relaxed = true) {
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
        val thrown = Assertions.assertThrows(
            IllegalStateException::class.java
        ) {
            useCase(request)
        }

        // Then
        assertThat(thrown.message).isEqualTo("Failed to calculate route cost")
    }
}
