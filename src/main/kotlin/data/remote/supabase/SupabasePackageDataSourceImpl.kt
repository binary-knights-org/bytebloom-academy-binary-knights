package data.remote.supabase

import data.local.dataholder.PackageRaw
import data.remote.datasource.RemotePackageDataSource
import data.mapper.packages.toRaw
import data.mapper.packages.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.packageDto.PackageResponseDto
import domain.exception.NetworkUnavailableException
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException

private const val PACKAGES_TABLE = "packages"

class SupabasePackageDataSourceImpl(
    private val httpClient: SupabaseHttpClient
) : RemotePackageDataSource {

    override suspend fun getRawPackages(): List<PackageRaw> {
        return try {
            val response = httpClient.get(PACKAGES_TABLE)
            val dtos: List<PackageResponseDto> = response.body()
            dtos.map { it.toRaw() }
        } catch (e: UnresolvedAddressException) {
            throw NetworkUnavailableException(
                message = e.message ?: "Network unreachable - Falling back to CSV",
                cause = e
            )
        } catch (e: IOException) {
            throw NetworkUnavailableException(
                message = e.message ?: "IO Network Error - Falling back to CSV",
                cause = e
            )
        }
    }

    override suspend fun createRawPackage(pkg: PackageRaw): Boolean {
        val response = httpClient.post(
            table = PACKAGES_TABLE,
            body = pkg.toRequestDto()
        )
        return response.status.isSuccess()
    }

    override suspend fun updateRawPackage(id: String, pkg: PackageRaw): Boolean {
        val response = httpClient.patch(
            table = PACKAGES_TABLE,
            id = id,
            body = pkg.toRequestDto(),
            primaryKey = "package_id"
        )
        return response.status.isSuccess()
    }

    override suspend fun deleteRawPackage(id: String): Boolean {
        val response = httpClient.delete(
            table = PACKAGES_TABLE,
            id = id,
            primaryKey = "package_id"
        )
        return response.status.isSuccess()
    }
}
