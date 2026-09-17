package domain.usecase.shipment

import domain.model.Package
import domain.model.Warehouse
import domain.repository.PackageRepository

class FindPackagesByOriginUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(originHub: Warehouse): List<Package> {
        return packageRepository.getAll().filter { it.originHub == originHub }
    }
}

