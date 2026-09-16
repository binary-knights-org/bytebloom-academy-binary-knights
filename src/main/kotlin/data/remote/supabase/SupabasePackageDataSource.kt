package data.remote.supabase

import data.dataholder.PackageRaw
import data.datasource.PackageDataSource
import data.mapper.packages.toRaw
import data.mapper.packages.toRequestDto
import data.remote.client.SupabaseHttpClient
import data.remote.dto.packageDto.PackageResponseDto
import io.ktor.client.call.body
import io.ktor.http.isSuccess

private const val PACKAGES_TABLE = "packages"

class SupabasePackageDataSource(
    private val httpClient: SupabaseHttpClient
) : PackageDataSource {

    override suspend fun getRawPackages(): List<PackageRaw> {
        val response = httpClient.get(PACKAGES_TABLE)
        val dtos: List<PackageResponseDto> = response.body()

        return dtos.map { it.toRaw() }
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
