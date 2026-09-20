package domain.usecase.crud.packages

import domain.exception.DatabaseConflictException
import domain.repository.PackageRepository

class DeletePackageUseCase(
    private val packageRepository: PackageRepository,
) {
    suspend operator fun invoke(id: String): Result<Unit> {

        return runCatching { packageRepository.delete(id) }.fold(
            onSuccess = { isDeleted ->
                if (isDeleted) {
                    Result.success(Unit)
                } else {
                    Result.failure(DatabaseConflictException("Failed to delete package with ID '$id' from database."))
                }
            },
            onFailure = { error ->
                Result.failure(DatabaseConflictException("Failed to delete package: ${error.message}", error))
            }
        )
    }
}
