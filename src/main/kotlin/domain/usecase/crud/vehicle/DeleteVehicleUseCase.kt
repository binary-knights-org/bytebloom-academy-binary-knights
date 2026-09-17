package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.VehicleValidator

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(id: String): Boolean {
        val isVaild = VehicleValidator.validateId(id) is ValidationResult.Success
        return vehicleRepository.delete(id) && isVaild
    }
}

