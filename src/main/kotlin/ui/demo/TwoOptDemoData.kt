package ui.demo

import domain.model.RegionalZone
import domain.model.Warehouse

    fun createTour(): List<Warehouse> {
        val origin = createWarehouse(
            name = "Central Warehouse",
            latitude = 32.2211,
            longitude = 35.2544
        )

        val deliveryStops = listOf(
            createWarehouse("Delivery 01", 32.2300, 35.2700),
            createWarehouse("Delivery 02", 32.2050, 35.2400),
            createWarehouse("Delivery 03", 32.2450, 35.2200),
            createWarehouse("Delivery 04", 32.1900, 35.2800),
            createWarehouse("Delivery 05", 32.2150, 35.2900),
            createWarehouse("Delivery 06", 32.2500, 35.2600),
            createWarehouse("Delivery 07", 32.1800, 35.2300),
            createWarehouse("Delivery 08", 32.2350, 35.3000),
            createWarehouse("Delivery 09", 32.2000, 35.2100),
            createWarehouse("Delivery 10", 32.2600, 35.2400),
            createWarehouse("Delivery 11", 32.2250, 35.2000),
            createWarehouse("Delivery 12", 32.1750, 35.2700),
            createWarehouse("Delivery 13", 32.2550, 35.2900),
            createWarehouse("Delivery 14", 32.1950, 35.2600),
            createWarehouse("Delivery 15", 32.2400, 35.2350)
        )

        return listOf(origin) + deliveryStops + origin
    }

    private fun createWarehouse(
        name: String,
        latitude: Double,
        longitude: Double
    ): Warehouse = Warehouse(
        name = name,
        regionalZone = RegionalZone.NORTH,
        latitude = latitude,
        longitude = longitude
    )
