package ui.demo

import domain.model.Warehouse

class DefaultTwoOptDemoData : TwoOptDemoData() {

    override fun createOrigin(): Warehouse =
        createWarehouse(
            name = ORIGIN_NAME,
            latitude = ORIGIN_LATITUDE,
            longitude = ORIGIN_LONGITUDE
        )

    override fun createDeliveryStops(): List<Warehouse> =
        DELIVERY_STOPS.map { stop ->
            createWarehouse(
                name = stop.name,
                latitude = stop.latitude,
                longitude = stop.longitude
            )
        }

    private data class DeliveryStop(
        val name: String,
        val latitude: Double,
        val longitude: Double
    )

    private companion object {
        const val ORIGIN_NAME = "Central Warehouse"
        const val ORIGIN_LATITUDE = 32.2211
        const val ORIGIN_LONGITUDE = 35.2544

        private val DELIVERY_STOPS = listOf(
            DeliveryStop("Delivery 01", 32.2300, 35.2700),
            DeliveryStop("Delivery 02", 32.2050, 35.2400),
            DeliveryStop("Delivery 03", 32.2450, 35.2200),
            DeliveryStop("Delivery 04", 32.1900, 35.2800),
            DeliveryStop("Delivery 05", 32.2150, 35.2900),
            DeliveryStop("Delivery 06", 32.2500, 35.2600),
            DeliveryStop("Delivery 07", 32.1800, 35.2300),
            DeliveryStop("Delivery 08", 32.2350, 35.3000),
            DeliveryStop("Delivery 09", 32.2000, 35.2100),
            DeliveryStop("Delivery 10", 32.2600, 35.2400),
            DeliveryStop("Delivery 11", 32.2250, 35.2000),
            DeliveryStop("Delivery 12", 32.1750, 35.2700),
            DeliveryStop("Delivery 13", 32.2550, 35.2900),
            DeliveryStop("Delivery 14", 32.1950, 35.2600),
            DeliveryStop("Delivery 15", 32.2400, 35.2350)
        )
    }
}
