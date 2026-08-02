package domain.builder

import dataholder.FleetRaw
import dataholder.PackageRaw
import dataholder.RouteRaw
import dataholder.WarehouseRaw

data class GraphRawData(
    val rawFleet: List<FleetRaw>,
    val rawPackages: List<PackageRaw>,
    val rawRoutes: List<RouteRaw>,
    val rawWarehouses: List<WarehouseRaw>
)
