package domain.usecase.crud.route

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.exception.ResourceNotFoundException
import domain.model.Route
import domain.model.input.UpdateRouteInput
import domain.repository.RouteRepository
import domain.validator.routes.UpdateRouteValidator

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: UpdateRouteValidator
) {
    suspend operator fun invoke(input: UpdateRouteInput): Result<Route> {
        val validation = validator.validate(input)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

        return runCatching { routeRepository.getById(input.id) }.fold(
            onSuccess = { existing ->
                if (existing == null) {
                    Result.failure(ResourceNotFoundException("Route with ID '${input.id}' was not found."))
                } else {
                    executeUpdate(existing, input)
                }
            },
            onFailure = { error ->
                Result.failure(
                    DatabaseConflictException(
                        "Failed to retrieve route for update: ${error.message}", error
                    )
                )
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
                            Result.failure(DatabaseConflictException("Failed to update route in database."))
                        }
                    },
                    onFailure = { error ->
                        Result.failure(DatabaseConflictException("Failed to update route: ${error.message}", error))
                    }
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
