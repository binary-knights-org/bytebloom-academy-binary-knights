package domain.usecase.crud.route

import domain.model.exception.OperationFailedException
import data.exception.translateDataError
import domain.model.exception.EntityValidationException
import domain.model.Route
import domain.model.input.CreateRouteInput
import domain.repository.RouteRepository
import domain.validator.ValidationResult
import domain.validator.routes.CreateRouteValidator

class CreateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: CreateRouteValidator
) {
    suspend operator fun invoke(input: CreateRouteInput): Result<Route> {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid)
            return Result.failure(EntityValidationException(validation.violations))

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
                            Result.failure(OperationFailedException())
                        }
                    },
                    onFailure = { error ->
                        Result.failure(translateDataError(error, "create", "route"))
                    }
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
