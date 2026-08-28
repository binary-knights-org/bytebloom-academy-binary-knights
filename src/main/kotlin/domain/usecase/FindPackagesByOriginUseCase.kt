package domain.usecase

import domain.model.Package
import domain.model.Warehouse
import domain.repository.PackageRepository

class FindPackagesByOriginUseCase(
    private val packageRepository: PackageRepository
) {
    operator fun invoke(originHub: Warehouse): List<Package> {
        return packageRepository.getAllPackages().filter { it.originHub == originHub }
    }
}

