package domain.usecase.crud.route

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.repository.RouteRepository
import domain.validator.routes.RouteIdValidator

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository,
    private val idValidator: RouteIdValidator
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        val validation = idValidator.validate(id)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

        return runCatching { routeRepository.delete(id) }.fold(
            onSuccess = { isDeleted ->
                if (isDeleted) {
                    Result.success(Unit)
                } else {
                    Result.failure(DatabaseConflictException("Failed to delete route with ID '$id' from database."))
                }
            },
            onFailure = { error ->
                Result.failure(DatabaseConflictException("Failed to delete route: ${error.message}", error))
            }
        )
    }
}
