package domain.usecase.crud.vehicle

import domain.model.exception.OperationFailedException
import domain.repository.VehicleRepository

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {

        return runCatching { vehicleRepository.delete(id) }.fold(
            onSuccess = { isDeleted ->
                if (isDeleted) {
                    Result.success(Unit)
                } else {
                    Result.failure(OperationFailedException())
                }
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
