package domain.usecase.telemetry

import com.google.common.truth.Truth.assertThat
import domain.model.telemetry.FleetTrackingCriteria
import domain.model.telemetry.VehicleTelemetry
import domain.service.TelemetryStreamingService
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class TrackActiveFleetUseCaseTest {

    private val telemetryService = mockk<TelemetryStreamingService>()
    private val useCase = TrackActiveFleetUseCase(telemetryService)

    @Test
    fun `should consume telemetry flow and emit notification for stationary vehicle`() = runTest {
        // Given
        val telemetry = listOf(
            VehicleTelemetry("TRK-101", 31.5, 34.4, speedKmh = 0.0, fuelPercentage = 80.0)
        )
        every { telemetryService.streamTelemetry(any(), any()) } returns flowOf(*telemetry.toTypedArray())

        // When
        val notifications = useCase().toList()

        // Then
        assertThat(notifications).hasSize(1)
        val notification = notifications.first()
        assertThat(notification.vehicleId).isEqualTo("TRK-101")
        assertThat(notification.delayMinutes).isGreaterThan(0)
        assertThat(notification.reason).contains("stationary")
    }

    @Test
    fun `should filter out normal operating vehicles and emit only delayed vehicles`() = runTest {
        // Given
        val telemetry = listOf(
            VehicleTelemetry("TRK-NORMAL", 31.5, 34.4, speedKmh = 85.0, fuelPercentage = 75.0),
            VehicleTelemetry("TRK-SLOW", 31.9, 35.2, speedKmh = 18.0, fuelPercentage = 60.0),
            VehicleTelemetry("TRK-FAST", 32.2, 35.2, speedKmh = 90.0, fuelPercentage = 80.0)
        )
        every { telemetryService.streamTelemetry(any(), any()) } returns flowOf(*telemetry.toTypedArray())

        // When
        val notifications = useCase().toList()

        // Then
        assertThat(notifications.map { it.vehicleId }).containsExactly("TRK-SLOW")
        assertThat(notifications.first().reason).contains("congestion")
    }

    @Test
    fun `should emit delay notification for critical low fuel level`() = runTest {
        // Given
        val telemetry = listOf(
            VehicleTelemetry("TRK-LOW-FUEL", 31.5, 34.4, speedKmh = 70.0, fuelPercentage = 8.0)
        )
        every { telemetryService.streamTelemetry(any(), any()) } returns flowOf(*telemetry.toTypedArray())

        // When
        val notifications = useCase().toList()

        // Then
        assertThat(notifications).hasSize(1)
        val notification = notifications.first()
        assertThat(notification.vehicleId).isEqualTo("TRK-LOW-FUEL")
        assertThat(notification.reason).contains("Low fuel")
    }

    @Test
    fun `should filter updates by target vehicle IDs when specified`() = runTest {
        // Given
        val telemetry = listOf(
            VehicleTelemetry("TRK-TARGET", 31.5, 34.4, speedKmh = 10.0, fuelPercentage = 50.0),
            VehicleTelemetry("TRK-IGNORED", 31.9, 35.2, speedKmh = 10.0, fuelPercentage = 50.0)
        )
        every { telemetryService.streamTelemetry(any(), any()) } returns flowOf(*telemetry.toTypedArray())

        val criteria = FleetTrackingCriteria(
            targetVehicleIds = setOf("TRK-TARGET")
        )

        // When
        val notifications = useCase(criteria).toList()

        // Then
        assertThat(notifications.map { it.vehicleId }).containsExactly("TRK-TARGET")
    }

    @Test
    fun `should respect minimum delay minutes threshold`() = runTest {
        // Given
        val telemetry = listOf(
            VehicleTelemetry("TRK-SLOW", 31.5, 34.4, speedKmh = 29.0, fuelPercentage = 50.0)
        )
        every { telemetryService.streamTelemetry(any(), any()) } returns flowOf(*telemetry.toTypedArray())

        // When
        // If minDelayMinutes is very high, smaller delays are suppressed
        val criteria = FleetTrackingCriteria(minDelayMinutes = 50)
        val notifications = useCase(criteria).toList()

        // Then
        assertThat(notifications).isEmpty()
    }

    @Test
    fun `should handle empty telemetry stream reactively`() = runTest {
        // Given
        every { telemetryService.streamTelemetry(any(), any()) } returns flowOf()

        // When
        val notifications = useCase().toList()

        // Then
        assertThat(notifications).isEmpty()
    }
}
