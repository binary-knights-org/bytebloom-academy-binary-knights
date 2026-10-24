package ui.demo

import domain.model.RegionalZone
import domain.model.Warehouse

abstract class TwoOptDemoData {

    fun createTour(): List<Warehouse> {
        val origin = createOrigin()
        val deliveryStops = createDeliveryStops()

        return buildList {
            add(origin)
            addAll(deliveryStops)
            add(origin)
        }
    }

    protected abstract fun createOrigin(): Warehouse

    protected abstract fun createDeliveryStops(): List<Warehouse>

    protected fun createWarehouse(
        name: String,
        latitude: Double,
        longitude: Double
    ): Warehouse = Warehouse(
        name = name,
        regionalZone = RegionalZone.NORTH,
        latitude = latitude,
        longitude = longitude
    )
}
