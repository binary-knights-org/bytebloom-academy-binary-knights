package data.reader

import data.dataholder.VehicleRaw
import java.io.File

class CsvFileWriter {

    fun writeVehicles(
        filePath: String,
        vehicles: List<VehicleRaw>
    ) {
        File(filePath).printWriter().use { writer ->
            writer.println("vehicleId,currentHubId,maxCapacityKg,costPerKm")

            vehicles.forEach { vehicle ->
                vehicle.vehicleIds.forEach { vehicleId ->
                    writer.println(
                        "$vehicleId," +
                                "${vehicle.currentHubId}," +
                                "${vehicle.maxCapacityKg}," +
                                "${vehicle.costPerKm}"
                    )
                }
            }
        }
    }
}
