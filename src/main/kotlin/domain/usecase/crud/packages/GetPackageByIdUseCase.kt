package domain.usecase.crud.packages

import domain.model.exception.ResourceNotFoundException
import data.exception.translateDataError
import domain.model.Package
import domain.repository.PackageRepository

class GetPackageByIdUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(id: String): Result<Package> {

        return runCatching { packageRepository.getById(id) }.fold(
            onSuccess = { pkg ->
                if (pkg != null) {
                    Result.success(pkg)
                } else {
                    Result.failure(ResourceNotFoundException())
                }
            },
            onFailure = { error -> Result.failure(translateDataError(error, "fetch", "package")) }
        )
    }
}
