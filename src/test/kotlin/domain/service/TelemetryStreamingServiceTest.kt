package domain.service

import com.google.common.truth.Truth.assertThat
import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse
import domain.model.telemetry.VehicleTelemetry
import domain.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class TelemetryStreamingServiceTest {

    @Test
    fun `streamTelemetry should return a cold flow that emits values upon collection`() = runTest {
        val service = TelemetryStreamingService()

        val flow = service.streamTelemetry(intervalMs = 0L, emissionCount = 2)
        val emissions = flow.toList()

        assertThat(emissions).isNotEmpty()
        assertThat(emissions.size).isAtLeast(5)
    }

    @Test
    fun `streamTelemetry should emit GPS coordinates, speed, and fuel percentages`() = runTest {
        val service = TelemetryStreamingService()

        val emissions = service.streamTelemetry(intervalMs = 0L, emissionCount = 1).toList()

        for (update in emissions) {
            assertThat(update.vehicleId).isNotEmpty()
            assertThat(update.latitude).isIn(
                com.google.common.collect.Range.closed(
                    VehicleTelemetry.MIN_LATITUDE,
                    VehicleTelemetry.MAX_LATITUDE
                )
            )
            assertThat(update.longitude).isIn(
                com.google.common.collect.Range.closed(
                    VehicleTelemetry.MIN_LONGITUDE,
                    VehicleTelemetry.MAX_LONGITUDE
                )
            )
            assertThat(update.speedKmh).isAtLeast(0.0)
            assertThat(update.fuelPercentage).isIn(
                com.google.common.collect.Range.closed(
                    VehicleTelemetry.MIN_FUEL,
                    VehicleTelemetry.MAX_FUEL
                )
            )
        }
    }

    @Test
    fun `streamTelemetry should populate initial states from vehicle repository when available`() = runTest {
        val vehicleRepo = mockk<VehicleRepository>()
        val hub = Warehouse(
            id = "WH-1",
            name = "Test Hub",
            regionalZone = RegionalZone.CENTRAL,
            latitude = 31.8,
            longitude = 35.1
        )
        val vehicles = listOf(
            Vehicle(id = "TRK-REPO-1", maxCapacityKg = 1000.0, costPerKm = 2.5, currentHub = hub)
        )
        coEvery { vehicleRepo.getAll() } returns vehicles

        val service = TelemetryStreamingService(vehicleRepository = vehicleRepo)
        val emissions = service.streamTelemetry(intervalMs = 0L, emissionCount = 1).toList()

        assertThat(emissions.map { it.vehicleId }).contains("TRK-REPO-1")
    }

    @Test
    fun `streamTelemetry should support multiple independent collectors as a cold flow`() = runTest {
        val service = TelemetryStreamingService()

        val collector1 = service.streamTelemetry(intervalMs = 0L, emissionCount = 1).take(2).toList()
        val collector2 = service.streamTelemetry(intervalMs = 0L, emissionCount = 1).take(2).toList()

        assertThat(collector1).hasSize(2)
        assertThat(collector2).hasSize(2)
    }
}
