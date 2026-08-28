package domain.usecase

import domain.model.Package
import domain.repository.PackageRepository

class GetAllPackagesUseCase(
    private val packageRepository: PackageRepository
) {
    operator fun invoke(): List<Package> {
        return packageRepository.getAllPackages()
    }
}
