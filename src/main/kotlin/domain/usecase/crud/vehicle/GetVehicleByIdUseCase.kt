package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.VehicleValidator

class GetVehicleByIdUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(id: String): Vehicle? {
        val isVaild = VehicleValidator.validateId(id) is ValidationResult.Success
        return  if (isVaild) vehicleRepository.getById(id) else null
    }
}

