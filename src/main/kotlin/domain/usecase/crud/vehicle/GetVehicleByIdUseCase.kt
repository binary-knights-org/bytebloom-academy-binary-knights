package domain.usecase.crud.vehicle

import domain.exception.VehicleException
import domain.model.Vehicle
import domain.repository.VehicleRepository

class GetVehicleByIdUseCase(private val vehicleRepository: VehicleRepository) {
suspend  operator fun invoke(id: String): Vehicle {
        return vehicleRepository.getByID(id)
            ?: throw VehicleException.NotFound(id)
    }
}
