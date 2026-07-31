package domain.builder

import dataholder.FleetRaw
import dataholder.PackageRaw
import dataholder.RouteRaw
import dataholder.WarehouseRaw

class NetworkDataLoader(val builder: NetworkBuilder) {

    fun loadData(
        rawFleets: List<FleetRaw>,
        rawPackages: List<PackageRaw>,
        rawRoutes: List<RouteRaw>,
        rawWarehouses: List<WarehouseRaw>
    ) {
        buildWarehouses(rawWarehouses)
        buildVehicles(rawFleets)
        buildPackages(rawPackages)
        buildRoutes(rawRoutes)
    }


    private fun buildWarehouses(rawWarehouses: List<WarehouseRaw>) {
        for (raw in rawWarehouses) {
            builder.buildWarehouse(
                id = raw.hubId,
                name = raw.hubName,
                regionalZone = raw.regionalZone
            )
        }
    }

    private fun buildVehicles(rawFleets: List<FleetRaw>) {
        for (fleet in rawFleets) {
            for (vId in fleet.vehicleIds) {
                builder.buildVehicle(
                    id = vId,
                    maxCapacityKg = fleet.maxCapacityKg ?: 0.0,
                    costPerKm = fleet.costPerKm ?: 0.0,
                    hubId = fleet.currentHubId
                )
            }
        }
    }

    private fun buildPackages(rawPackages: List<PackageRaw>) {
        for (rawPkg in rawPackages) {
            builder.buildPackage(
                id = rawPkg.packageId,
                weight = rawPkg.weight,
                priority = rawPkg.priority,
                originId = rawPkg.originHubId,
                destinationId = rawPkg.destinationHubId
            )
        }
    }

    private fun buildRoutes(rawRoutes: List<RouteRaw>) {
        for (rawRoute in rawRoutes) {
            builder.buildRoute(
                id = rawRoute.routeId,
                distanceKm = rawRoute.distanceKm,
                typicalDelayMin = rawRoute.typicalDelayMin,
                originId = rawRoute.originHubId,
                destinationId = rawRoute.destinationHubId
            )
        }
    }
}