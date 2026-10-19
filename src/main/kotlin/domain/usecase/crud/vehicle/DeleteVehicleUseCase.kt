package domain.usecase.crud.vehicle

import domain.repository.VehicleRepository

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> =
        runCatching {
            vehicleRepository.delete(id)
        }
}
