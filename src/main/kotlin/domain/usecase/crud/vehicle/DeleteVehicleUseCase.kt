package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.repository.VehicleRepository

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(id: String): Boolean  {
        return vehicleRepository.delete(id)
    }
}
