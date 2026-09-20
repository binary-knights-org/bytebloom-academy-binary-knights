package domain.usecase.crud.packages

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.exception.ResourceNotFoundException
import domain.model.Package
import domain.repository.PackageRepository

class GetPackageByIdUseCase(
    private val packageRepository: PackageRepository,
) {
    suspend operator fun invoke(id: String): Result<Package> {

        return runCatching { packageRepository.getById(id) }.fold(
            onSuccess = { pkg ->
                if (pkg != null) {
                    Result.success(pkg)
                } else {
                    Result.failure(ResourceNotFoundException("Package with ID '$id' was not found."))
                }
            },
            onFailure = { error ->
                Result.failure(
                    DatabaseConflictException(
                        "Failed to fetch package with ID '$id': ${error.message}", error
                    )
                )
            }
        )
    }
}
