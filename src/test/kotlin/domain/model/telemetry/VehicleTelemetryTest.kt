package domain.model.telemetry

import com.google.common.truth.Truth.assertThat
import domain.model.exception.InvalidFuelPercentageException
import domain.model.exception.InvalidLatitudeException
import domain.model.exception.InvalidLongitudeException
import domain.model.exception.InvalidSpeedException
import domain.model.exception.InvalidVehicleIdException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VehicleTelemetryTest {

    @Test
    fun `should create valid VehicleTelemetry successfully`() {
        val telemetry = VehicleTelemetry(
            vehicleId = "TRK-001",
            latitude = 31.5,
            longitude = 34.4,
            speedKmh = 65.0,
            fuelPercentage = 80.0
        )

        assertThat(telemetry.vehicleId).isEqualTo("TRK-001")
        assertThat(telemetry.latitude).isEqualTo(31.5)
        assertThat(telemetry.longitude).isEqualTo(34.4)
        assertThat(telemetry.speedKmh).isEqualTo(65.0)
        assertThat(telemetry.fuelPercentage).isEqualTo(80.0)
        assertThat(telemetry.isStationary).isFalse()
        assertThat(telemetry.isLowFuel).isFalse()
    }

    @Test
    fun `isStationary should return true when speed is zero`() {
        val telemetry = VehicleTelemetry(
            vehicleId = "TRK-002",
            latitude = 32.0,
            longitude = 35.0,
            speedKmh = 0.0,
            fuelPercentage = 50.0
        )

        assertThat(telemetry.isStationary).isTrue()
    }

    @Test
    fun `isLowFuel should return true when fuel is below threshold`() {
        val telemetry = VehicleTelemetry(
            vehicleId = "TRK-003",
            latitude = 32.0,
            longitude = 35.0,
            speedKmh = 50.0,
            fuelPercentage = 10.0
        )

        assertThat(telemetry.isLowFuel).isTrue()
    }

    @Test
    fun `should throw exception when vehicleId is blank`() {
        assertThrows<InvalidVehicleIdException> {
            VehicleTelemetry(
                vehicleId = "   ",
                latitude = 32.0,
                longitude = 35.0,
                speedKmh = 50.0,
                fuelPercentage = 50.0
            )
        }
    }

    @Test
    fun `should throw exception when latitude is out of bounds`() {
        assertThrows<InvalidLatitudeException> {
            VehicleTelemetry(
                vehicleId = "TRK-001",
                latitude = 95.0,
                longitude = 35.0,
                speedKmh = 50.0,
                fuelPercentage = 50.0
            )
        }
    }

    @Test
    fun `should throw exception when longitude is out of bounds`() {
        assertThrows<InvalidLongitudeException> {
            VehicleTelemetry(
                vehicleId = "TRK-001",
                latitude = 30.0,
                longitude = -190.0,
                speedKmh = 50.0,
                fuelPercentage = 50.0
            )
        }
    }

    @Test
    fun `should throw exception when speed is negative`() {
        assertThrows<InvalidSpeedException> {
            VehicleTelemetry(
                vehicleId = "TRK-001",
                latitude = 30.0,
                longitude = 35.0,
                speedKmh = -5.0,
                fuelPercentage = 50.0
            )
        }
    }

    @Test
    fun `should throw exception when fuel is above 100 percent`() {
        assertThrows<InvalidFuelPercentageException> {
            VehicleTelemetry(
                vehicleId = "TRK-001",
                latitude = 30.0,
                longitude = 35.0,
                speedKmh = 50.0,
                fuelPercentage = 105.0
            )
        }
    }
}
