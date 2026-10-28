package domain.usecase.search

import domain.model.Route
import domain.repository.RouteRepository

class SearchRoutesUseCase(
    routeRepository: RouteRepository
) : TextSearchUseCase<Route>(
    loadAll = routeRepository::getAll,
    matches = { route, query ->
        matchesAny(
            query,
            route.id,
            route.originHub.id,
            route.originHub.name,
            route.destinationHub.id,
            route.destinationHub.name
        )
    }
)
