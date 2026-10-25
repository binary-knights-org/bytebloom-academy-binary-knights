package domain.usecase.vehicle

import domain.model.Warehouse
import domain.model.exception.EntityValidationException
import domain.model.exception.ResourceNotFoundException
import domain.model.input.UpdateVehicleInput
import domain.model.validation.ValidationResult
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import domain.validator.vehicle.UpdateVehicleValidator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class AddVehicleToHubUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val vehicleRepository: VehicleRepository,
    private val validator: UpdateVehicleValidator
) {

    suspend operator fun invoke(
        warehouseId: String,
        vehicleId: String
    ): Result<Warehouse> = runCatching {

        val (warehouse, vehicle) = coroutineScope {
            val warehouseDeferred = async {
                warehouseRepository.getById(warehouseId) ?: throw ResourceNotFoundException()
            }
            val vehicleDeferred = async {
                vehicleRepository.getById(vehicleId) ?: throw ResourceNotFoundException()
            }
            warehouseDeferred.await() to vehicleDeferred.await()
        }

        val input = UpdateVehicleInput(id = vehicle.id, currentHub = warehouse)
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid) {
            throw EntityValidationException(validation.violations)
        }

        val updatedVehicle = vehicle.copy(currentHub = warehouse)
        vehicleRepository.update(updatedVehicle)

        warehouse.addVehicle(updatedVehicle)
        warehouse
    }
}
