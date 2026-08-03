package domain.builder

import data.dataholder.FleetRaw
import data.dataholder.PackageRaw
import data.dataholder.RouteRaw
import data.dataholder.WarehouseRaw

data class GraphData(
    val rawFleet: List<FleetRaw>,
    val rawPackages: List<PackageRaw>,
    val rawRoutes: List<RouteRaw>,
    val rawWarehouses: List<WarehouseRaw>
)
