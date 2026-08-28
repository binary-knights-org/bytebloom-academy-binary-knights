package domain.usecase

import domain.model.Package
import domain.repository.PackageRepository

class FindPackagesByPriorityUseCase(
    private val packageRepository: PackageRepository
) {
    operator fun invoke(priority: String): List<Package> {
        return packageRepository.getAllPackages().filter { it.priority == priority }
    }

}
