package domain.usecase.search


import domain.model.Package
import domain.model.RegionalZone
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse
import domain.model.toPriority
import domain.service.SearchState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

private const val SETTLE_MS = 500L

internal fun aWarehouse(
    id: String,
    name: String = "Hub-${id.takeLast(3)}",
    zone: String = "CENTRAL"
) = Warehouse(
    id = id,
    name = name,
    regionalZone = RegionalZone.valueOf(zone),
    latitude = 30.0,
    longitude = 35.0
)

internal fun aPackage(
    id: String,
    origin: Warehouse,
    destination: Warehouse,
    priority: String = "STANDARD"
) = Package(
    id = id,
    weight = 100.0,
    priority = priority.toPriority(),
    originHub = origin,
    destinationHub = destination
)

internal fun aRoute(
    id: String,
    origin: Warehouse,
    destination: Warehouse
) = Route(
    id = id,
    distanceKm = 10.0,
    typicalDelayMin = 5,
    originHub = origin,
    destinationHub = destination
)

internal fun aVehicle(
    id: String,
    hub: Warehouse
) = Vehicle(
    id = id,
    maxCapacityKg = 1_000.0,
    costPerKm = 1.0,
    currentHub = hub
)

internal suspend fun <T> TextSearchUseCase<T>.searchFor(query: String): List<T> {
    val states = invoke(
        flow {
            emit(query)
            delay(SETTLE_MS)
        }
    ).toList()

    return states.filterIsInstance<SearchState.Success<*>>().single().items as List<T>
}
