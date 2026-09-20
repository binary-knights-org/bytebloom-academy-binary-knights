package domain.usecase.crud.route

import domain.exception.DatabaseConflictException
import domain.repository.RouteRepository

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository,
) {
    suspend operator fun invoke(id: String): Result<Unit> {

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
