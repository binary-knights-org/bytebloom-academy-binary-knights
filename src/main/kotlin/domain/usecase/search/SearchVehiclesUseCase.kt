package domain.usecase.search

import domain.model.Vehicle
import domain.repository.VehicleRepository

class SearchVehiclesUseCase(
    vehicleRepository: VehicleRepository
) : TextSearchUseCase<Vehicle>(
    loadAll = vehicleRepository::getAll,
    matches = { vehicle, query ->
        matchesAny(
            query,
            vehicle.id,
            vehicle.currentHub.id,
            vehicle.currentHub.name
        )
    }
)
