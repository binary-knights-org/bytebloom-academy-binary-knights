package domain.usecase.crud.route

import domain.repository.RouteRepository
import domain.model.exception.DatabaseOperationFailedException
import domain.validator.ValidationResult
import domain.validator.routes.RouteIdValidator

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository,
    private val idValidator: RouteIdValidator
) {
    suspend operator fun invoke(id: String): ValidationResult<Unit> {
        val validationResult = idValidator.validate(id)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        val isDeleted = routeRepository.delete(id)
        return if (isDeleted) {
            ValidationResult.Success(Unit)
        } else {
            ValidationResult.Failure(listOf(DatabaseOperationFailedException("delete", "route")))
        }
    }
}
