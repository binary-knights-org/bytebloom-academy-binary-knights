package domain.usecase.crud.packages

import domain.model.Package
import domain.repository.PackageRepository

class GetPackageByIdUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(id: String): Package? {
        return packageRepository.getById(id)
    }
}
