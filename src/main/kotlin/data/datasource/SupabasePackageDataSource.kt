package data.datasource

import data.dataholder.PackageRaw
import data.mapper.toRaw
import data.remote.SupabaseHttpClient
import data.remote.dto.PackageDto
import io.ktor.client.call.body
import kotlinx.coroutines.runBlocking

private const val PACKAGES_TABLE = "packages"

class SupabasePackageDataSource : PackageDataSource {

    override fun getRawPackages(): List<PackageRaw> {
        return runBlocking {
            val response = SupabaseHttpClient.get(PACKAGES_TABLE)
            val dtos: List<PackageDto> = response.body()
            dtos.map { it.toRaw() }
        }
    }
}