package data.remote.supabase

import data.exception.DataException
import data.exception.NetworkUnavailableException
import data.local.dataholder.PackageRaw
import data.mapper.packages.toRaw
import data.mapper.packages.toRequestDto
import data.remote.base.BaseRemoteDataSource
import data.remote.datasource.RemotePackageDataSource
import data.remote.dto.packageDto.PackageResponseDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.ktor.util.network.UnresolvedAddressException
import java.io.IOException

private const val PACKAGES_TABLE = "packages"
private const val PACKAGES_PRIMARY_KEY = "package_id"

class SupabasePackageDataSourceImpl(
    private val supabase: SupabaseClient
) : BaseRemoteDataSource(), RemotePackageDataSource {

    override suspend fun getRawPackages(): List<PackageRaw> =
        retryWithBackoff {
            supabase
                .from(PACKAGES_TABLE)
                .select()
                .decodeList<PackageResponseDto>()
                .map { it.toRaw() }
        }.getOrElse { error ->
            when (error) {
                is UnresolvedAddressException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_UNREACHABLE_FETCH
                    )
                }

                is IOException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_IO_ERROR_FETCH
                    )
                }

                else -> throw error
            }
        }

    override suspend fun createRawPackage(
        pkg: PackageRaw
    ): Boolean =
        retryWithBackoff {
            try {
                supabase
                    .from(PACKAGES_TABLE)
                    .insert(pkg.toRequestDto())

                true
            } catch (_: RestException) {
                false
            }
        }.getOrElse { error ->
            when (error) {
                is UnresolvedAddressException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_UNREACHABLE_CREATE
                    )
                }

                is IOException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_IO_ERROR_CREATE
                    )
                }

                else -> throw error
            }
        }

    override suspend fun updateRawPackage(
        id: String,
        pkg: PackageRaw
    ): Boolean =
        retryWithBackoff {
            try {
                supabase
                    .from(PACKAGES_TABLE)
                    .update(pkg.toRequestDto()) {
                        filter {
                            eq(PACKAGES_PRIMARY_KEY, id)
                        }
                    }

                true
            } catch (_: RestException) {
                false
            }
        }.getOrElse { error ->
            when (error) {
                is UnresolvedAddressException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_UNREACHABLE_UPDATE
                    )
                }

                is IOException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_IO_ERROR_UPDATE
                    )
                }

                else -> throw error
            }
        }

    override suspend fun deleteRawPackage(
        id: String
    ): Boolean =
        retryWithBackoff {
            try {
                supabase
                    .from(PACKAGES_TABLE)
                    .delete {
                        filter {
                            eq(PACKAGES_PRIMARY_KEY, id)
                        }
                    }

                true
            } catch (_: RestException) {
                false
            }
        }.getOrElse { error ->
            when (error) {
                is UnresolvedAddressException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_UNREACHABLE_DELETE
                    )
                }

                is IOException -> {
                    throw NetworkUnavailableException(
                        error.message ?: DataException.NETWORK_IO_ERROR_DELETE
                    )
                }

                else -> throw error
            }
        }
}
