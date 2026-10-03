package edu.logiroute.logiroute.ui.data

import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.Warehouse

val destinationWarehouse = Warehouse(
    "WH-096",
    "HUB-096",
    RegionalZone.WEST,
    34.60,
    -81.62
)
val originWarehouse = Warehouse(
    "WH-001",
    "Hub-001",
    RegionalZone.CENTRAL,
    37.91,
    -88.46
)

val urgentPriorityPackage = Package(
    "PKG-000001",
    12.5,
    Priority.URGENT,
    originWarehouse,
    destinationWarehouse
)
val lowPriorityPackage = Package(
    "PKG-000199",
    281.07,
    Priority.LOW,
    originWarehouse,
    destinationWarehouse
)
val standardPriorityPackage = Package(
    "PKG-000200",
    111.67,
    Priority.STANDARD,
    originWarehouse,
    destinationWarehouse
)

val vehicle = Vehicle(
    "TRK-0012",
    7151.99 ,
    1.70,
    originWarehouse
)

val activeWarehouse= Warehouse(
    "WH-077",
    "Hub-077",
    RegionalZone.NORTH,
    44.27,
    -84.12
).apply {
    addPackage(urgentPriorityPackage)
    addPackage(standardPriorityPackage)
    addPackage(lowPriorityPackage)
    addVehicle(vehicle)
}
val emptyWarehouse= Warehouse(
    "WH-002",
    "Hub-002",
    RegionalZone.CENTRAL,
    37.91,
    -88.46
)


