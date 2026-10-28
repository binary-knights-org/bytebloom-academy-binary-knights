package domain.usecase.search

import domain.model.Package
import domain.repository.PackageRepository

class SearchPackagesUseCase(
    packageRepository: PackageRepository
) : TextSearchUseCase<Package>(
    loadAll = packageRepository::getAll,
    matches = { pkg, query ->
        matchesAny(
            query,
            pkg.id,
            pkg.originHub.id,
            pkg.originHub.name,
            pkg.destinationHub.id,
            pkg.destinationHub.name,
            pkg.priority.name
        )
    }
)
