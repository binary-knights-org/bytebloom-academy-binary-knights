package data.remote

import data.dataholder.PackageRaw
import data.datasource.PackageDataSource
import data.mapper.toRaw
import io.ktor.client.call.body
import data.remote.dto.PackageResponseDto

private const val PACKAGES_TABLE = "packages"

class SupabasePackageDataSource (
    private val httpClient: SupabaseHttpClient
): PackageDataSource {
    override suspend fun getRawPackages(): List<PackageRaw> {
        val response = httpClient.get(PACKAGES_TABLE)
        val dtos: List<PackageResponseDto> = response.body()

        return dtos.map { it.toRaw() }
    }
}

