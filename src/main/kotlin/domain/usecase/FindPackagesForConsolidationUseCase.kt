package domain.usecase

import domain.model.Package
import domain.repository.PackageRepository

class FindPackagesForConsolidationUseCase(
    private val packageRepository: PackageRepository
) {

    operator fun invoke(): List<List<Package>> {
        return packageRepository.getAllPackages()
            .groupBy { it.originHub.id to it.destinationHub.id }
            .values.filter { it.size >= MIN_PACKAGES_FOR_CONSOLIDATION }
    }

    private companion object {
        const val MIN_PACKAGES_FOR_CONSOLIDATION = 2
    }
}