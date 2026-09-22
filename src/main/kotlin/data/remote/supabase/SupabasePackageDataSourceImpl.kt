package data.remote.supabase

import data.exception.DataException
import data.exception.NetworkUnavailableException
import data.local.dataholder.PackageRaw
import data.remote.datasource.RemotePackageDataSource
import data.mapper.packages.toRaw
import data.mapper.packages.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.packageDto.PackageResponseDto
import data.remote.base.BaseRemoteDataSource
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException

private const val PACKAGES_TABLE = "packages"

class SupabasePackageDataSourceImpl(
    private val httpClient: SupabaseHttpClient
) : BaseRemoteDataSource(), RemotePackageDataSource {

    override suspend fun getRawPackages(): List<PackageRaw> = retryWithBackoff {
        val response = httpClient.get(PACKAGES_TABLE)
        val dtos: List<PackageResponseDto> = response.body()
        dtos.map { it.toRaw() }
    }.getOrElse { e ->
        when (e) {
            is UnresolvedAddressException -> throw NetworkUnavailableException(
                e.message ?: DataException.NETWORK_UNREACHABLE_FETCH
            )

            is IOException -> throw NetworkUnavailableException(e.message ?: DataException.NETWORK_IO_ERROR_FETCH)
            else -> throw e
        }
    }

    override suspend fun createRawPackage(pkg: PackageRaw): Boolean = retryWithBackoff {
        val response = httpClient.post(table = PACKAGES_TABLE, body = pkg.toRequestDto())
        response.status.isSuccess()
    }.getOrElse { e ->
        when (e) {
            is UnresolvedAddressException -> throw NetworkUnavailableException(
                e.message ?: DataException.NETWORK_UNREACHABLE_CREATE
            )

            is IOException -> throw NetworkUnavailableException(e.message ?: DataException.NETWORK_IO_ERROR_CREATE)
            else -> throw e
        }
    }

    override suspend fun updateRawPackage(id: String, pkg: PackageRaw): Boolean = retryWithBackoff {
        val response =
            httpClient.patch(table = PACKAGES_TABLE, id = id, body = pkg.toRequestDto(), primaryKey = "package_id")
        response.status.isSuccess()
    }.getOrElse { e ->
        when (e) {
            is UnresolvedAddressException -> throw NetworkUnavailableException(
                e.message ?: DataException.NETWORK_UNREACHABLE_UPDATE
            )

            is IOException -> throw NetworkUnavailableException(e.message ?: DataException.NETWORK_IO_ERROR_UPDATE)
            else -> throw e
        }
    }

    override suspend fun deleteRawPackage(id: String): Boolean = retryWithBackoff {
        val response = httpClient.delete(table = PACKAGES_TABLE, id = id, primaryKey = "package_id")
        response.status.isSuccess()
    }.getOrElse { e ->
        when (e) {
            is UnresolvedAddressException -> throw NetworkUnavailableException(
                e.message ?: DataException.NETWORK_UNREACHABLE_DELETE
            )

            is IOException -> throw NetworkUnavailableException(e.message ?: DataException.NETWORK_IO_ERROR_DELETE)
            else -> throw e
        }
    }
}
