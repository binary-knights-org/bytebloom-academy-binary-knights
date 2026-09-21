package domain.usecase.crud.route

import domain.model.exception.OperationFailedException
import data.exception.translateDataError
import domain.model.exception.EntityValidationException
import domain.model.exception.ResourceNotFoundException
import domain.model.Route
import domain.model.input.UpdateRouteInput
import domain.repository.RouteRepository
import domain.validator.ValidationResult
import domain.validator.routes.UpdateRouteValidator

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: UpdateRouteValidator
) {
    suspend operator fun invoke(input: UpdateRouteInput): Result<Route> {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid)
            return Result.failure(EntityValidationException(validation.violations))

        return runCatching { routeRepository.getById(input.id) }.fold(
            onSuccess = { existing ->
                if (existing == null) {
                    Result.failure(ResourceNotFoundException())
                } else {
                    executeUpdate(existing, input)
                }
            },
            onFailure = { error ->
                Result.failure(translateDataError(error, "retrieve", "route"))
            }
        )
    }

    private suspend fun executeUpdate(existing: Route, input: UpdateRouteInput): Result<Route> {
        return runCatching {
            existing.copy(
                distanceKm = input.distanceKm ?: existing.distanceKm,
                typicalDelayMin = input.typicalDelayMin ?: existing.typicalDelayMin,
                originHub = input.originHub ?: existing.originHub,
                destinationHub = input.destinationHub ?: existing.destinationHub
            )
        }.fold(
            onSuccess = { updatedRoute ->
                runCatching { routeRepository.update(updatedRoute) }.fold(
                    onSuccess = { isUpdated ->
                        if (isUpdated) {
                            Result.success(updatedRoute)
                        } else {
                            Result.failure(OperationFailedException())
                        }
                    },
                    onFailure = { error ->
                        Result.failure(translateDataError(error, "update", "route"))
                    }
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
