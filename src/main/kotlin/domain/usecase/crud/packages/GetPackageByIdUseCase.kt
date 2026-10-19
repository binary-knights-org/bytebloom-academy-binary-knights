package domain.usecase.crud.packages

import domain.model.Package
import domain.model.exception.ResourceNotFoundException
import domain.repository.PackageRepository

class GetPackageByIdUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(id: String): Result<Package> =
        runCatching {
            packageRepository.getById(id)
                ?: throw ResourceNotFoundException()
        }
}
