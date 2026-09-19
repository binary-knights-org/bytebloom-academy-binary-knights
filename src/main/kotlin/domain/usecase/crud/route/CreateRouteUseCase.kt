package domain.usecase.crud.route

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.model.Route
import domain.model.input.CreateRouteInput
import domain.repository.RouteRepository
import domain.validator.routes.CreateRouteValidator

class CreateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: CreateRouteValidator
) {
    suspend operator fun invoke(input: CreateRouteInput): Result<Route> {
        val validation = validator.validate(input)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

        return runCatching {
            Route(
                id = input.id,
                distanceKm = input.distanceKm,
                typicalDelayMin = input.typicalDelayMin,
                originHub = input.originHub,
                destinationHub = input.destinationHub
            )
        }.fold(
            onSuccess = { route ->
                runCatching { routeRepository.create(route) }.fold(
                    onSuccess = { isCreated ->
                        if (isCreated) {
                            Result.success(route)
                        } else {
                            Result.failure(DatabaseConflictException("Failed to create route in database."))
                        }
                    },
                    onFailure = { error ->
                        Result.failure(DatabaseConflictException("Failed to create route: ${error.message}", error))
                    }
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
