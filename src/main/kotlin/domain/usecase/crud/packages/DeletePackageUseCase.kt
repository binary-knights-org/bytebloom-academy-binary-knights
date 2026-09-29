package domain.usecase.crud.packages

import domain.model.exception.OperationFailedException
import domain.repository.PackageRepository

class DeletePackageUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return runCatching { packageRepository.delete(id) }.fold(onSuccess = { isDeleted ->
            if (isDeleted) {
                Result.success(Unit)
            } else {
                Result.failure(OperationFailedException())
            }
        }, onFailure = { error -> Result.failure(error) })
    }
}
