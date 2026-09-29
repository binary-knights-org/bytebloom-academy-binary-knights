package domain.usecase.crud.route

import domain.model.exception.OperationFailedException
import domain.repository.RouteRepository

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository
) {
    public suspend operator fun invoke(id: String): Result<Unit> {
        return runCatching { routeRepository.delete(id) }.fold(
            onSuccess = { isDeleted ->
                if (isDeleted) {
                    Result.success(Unit)
                } else {
                    Result.failure(OperationFailedException())
                }
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
