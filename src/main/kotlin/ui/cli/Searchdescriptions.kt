package ui.cli

import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse

internal fun Package.describe() =
    "$id | ${originHub.name} -> ${destinationHub.name} | $weight kg"

internal fun Route.describe() =
    "$id | ${originHub.name} -> ${destinationHub.name} | $distanceKm km"

internal fun Vehicle.describe() =
    "$id | capacity $maxCapacityKg kg | hub ${currentHub.name}"

internal fun Warehouse.describe() =
    "$id | $name | $regionalZone"
