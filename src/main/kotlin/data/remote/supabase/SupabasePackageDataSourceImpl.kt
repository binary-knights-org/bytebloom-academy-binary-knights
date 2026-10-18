package data.remote.supabase

import data.exception.DataException
import data.local.dataholder.PackageRaw
import data.mapper.packages.toRaw
import data.mapper.packages.toRequestDto
import data.remote.base.BaseRemoteDataSource
import data.remote.datasource.RemotePackageDataSource
import data.remote.dto.packageDto.PackageResponseDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

private const val PACKAGES_TABLE = "packages"
private const val PACKAGES_PRIMARY_KEY = "package_id"

class SupabasePackageDataSourceImpl(
    private val supabase: SupabaseClient
) : BaseRemoteDataSource(), RemotePackageDataSource {

    override suspend fun getRawPackages(): List<PackageRaw> =
        safeRemoteCall(DataException.NETWORK_UNREACHABLE_FETCH, DataException.NETWORK_IO_ERROR_FETCH) {
            supabase.from(PACKAGES_TABLE)
                .select()
                .decodeList<PackageResponseDto>()
                .map { it.toRaw() }
        }

    override suspend fun createRawPackage(pkg: PackageRaw): PackageRaw =
        safeRemoteCall(DataException.NETWORK_UNREACHABLE_CREATE, DataException.NETWORK_IO_ERROR_CREATE) {
            supabase.from(PACKAGES_TABLE)
                .insert(pkg.toRequestDto()) { select() }
                .decodeSingle<PackageResponseDto>()
                .toRaw()
        }

    override suspend fun updateRawPackage(id: String, pkg: PackageRaw): PackageRaw =
        safeRemoteCall(DataException.NETWORK_UNREACHABLE_UPDATE, DataException.NETWORK_IO_ERROR_UPDATE) {
            supabase.from(PACKAGES_TABLE)
                .update(pkg.toRequestDto()) {
                    select()
                    filter { eq(PACKAGES_PRIMARY_KEY, id) }
                }
                .decodeSingle<PackageResponseDto>()
                .toRaw()
        }

    override suspend fun deleteRawPackage(id: String): Boolean =
        safeWriteCall(DataException.NETWORK_UNREACHABLE_DELETE, DataException.NETWORK_IO_ERROR_DELETE) {
            supabase.from(PACKAGES_TABLE).delete {
                filter { eq(PACKAGES_PRIMARY_KEY, id) }
            }
        }
}
