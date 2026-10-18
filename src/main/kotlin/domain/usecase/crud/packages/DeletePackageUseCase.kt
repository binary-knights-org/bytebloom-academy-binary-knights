package domain.usecase.crud.packages

import domain.repository.PackageRepository

class DeletePackageUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> =
        runCatching {
            packageRepository.delete(id)
        }
}
