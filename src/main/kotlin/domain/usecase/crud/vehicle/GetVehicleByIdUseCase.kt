package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.repository.VehicleRepository

class GetVehicleByIdUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(id: String): Vehicle? {
        return vehicleRepository.getById(id)
    }}
