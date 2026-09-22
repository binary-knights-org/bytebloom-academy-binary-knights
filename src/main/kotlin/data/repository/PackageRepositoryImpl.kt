package data.repository

import data.exception.NetworkUnavailableException
import data.local.datasource.CsvPackageDataSource
import data.remote.datasource.RemotePackageDataSource
import data.mapper.packages.toDomain
import data.mapper.packages.toRaw
import domain.model.Package
import domain.repository.PackageRepository
import domain.repository.WarehouseRepository

class PackageRepositoryImpl(
    private val remoteDataSource: RemotePackageDataSource,
    private val localDataSource: CsvPackageDataSource,
    private val warehouseRepository: WarehouseRepository
) : PackageRepository {

    private var packages: List<Package>? = null

    override suspend fun getAll(): List<Package> =
        packages ?: fetchPackagesFromSource().also { packages = it }

    override suspend fun getById(id: String): Package? =
        getAll().find { it.id == id }

    override suspend fun create(item: Package): Boolean =
        runCatching { remoteDataSource.createRawPackage(item.toRaw()) }
            .getOrDefault(false)
            .also { isSuccess ->
                if (isSuccess) packages = null
            }

    override suspend fun update(item: Package): Boolean =
        runCatching { remoteDataSource.updateRawPackage(item.id, item.toRaw()) }
            .getOrDefault(false)
            .also { isSuccess ->
                if (isSuccess) packages = null
            }

    override suspend fun delete(id: String): Boolean =
        runCatching { remoteDataSource.deleteRawPackage(id) }
            .getOrDefault(false)
            .also { isSuccess ->
                if (isSuccess) packages = null
            }

    private suspend fun fetchPackagesFromSource(): List<Package> {
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }

        return runCatching {
            remoteDataSource.getRawPackages().mapNotNull { it.toDomain(warehousesById) }
        }.getOrElse { e ->
            if (e is NetworkUnavailableException) {
                localDataSource.getAllPackages().mapNotNull { it.toDomain(warehousesById) }
            } else {
                throw e
            }
        }
    }
}
