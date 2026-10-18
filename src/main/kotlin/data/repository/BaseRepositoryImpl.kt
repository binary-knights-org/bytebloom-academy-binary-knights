package data.repository

import data.exception.NetworkUnavailableException
import data.exception.translateDataError
import domain.model.Warehouse
import domain.repository.WarehouseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.logging.Logger

abstract class BaseRepositoryImpl<T : Any>(
    private val entityName: String
) {
    protected var cache: List<T>? = null

    protected suspend fun <R> executeWriteOp(
        operation: String,
        action: suspend () -> R
    ): R = runCatching { action() }.fold(
        onSuccess = { result ->
            cache = null
            result
        },
        onFailure = { error ->
            throw translateDataError(error, operation, entityName)
        }
    )

    protected suspend fun <TRaw> fetchWithFallback(
        warehouseRepository: WarehouseRepository,
        remoteFetch: suspend () -> List<TRaw>,
        localFetch: suspend () -> List<TRaw>,
        mapToDomain: (TRaw, Map<String, Warehouse>) -> T?,
        logger: Logger
    ): List<T> = coroutineScope {
        val warehousesById = async { warehouseRepository.getAll().associateBy { it.id } }
        val remoteResult = runCatching { remoteFetch() }

        remoteResult
            .map { rawList -> rawList.mapNotNull { mapToDomain(it, warehousesById.await()) } }
            .getOrElse { error ->
                if (error is NetworkUnavailableException) {
                    logger.warning("Remote unavailable, falling back to local CSV for $entityName")
                    localFetch().mapNotNull { mapToDomain(it, warehousesById.await()) }
                } else {
                    throw translateDataError(error, "fetch", entityName)
                }
            }
    }
}
