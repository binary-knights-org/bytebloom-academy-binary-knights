package data.repository

import data.local.datasource.CsvPackageDataSource
import data.mapper.packages.toDomain
import data.mapper.packages.toRaw
import data.remote.datasource.RemotePackageDataSource
import domain.model.Package
import domain.repository.PackageRepository
import domain.repository.WarehouseRepository
import java.util.logging.Logger

class PackageRepositoryImpl(
    private val remoteDataSource: RemotePackageDataSource,
    private val localDataSource: CsvPackageDataSource,
    private val warehouseRepository: WarehouseRepository
) : BaseRepositoryImpl<Package>("package"), PackageRepository {

    private val logger = Logger.getLogger(PackageRepositoryImpl::class.java.simpleName)

    override suspend fun getAll(): List<Package> =
        cache ?: fetchPackagesFromSource().also { cache = it }

    override suspend fun getById(id: String): Package? =
        getAll().find { it.id == id }

    override suspend fun create(item: Package): Package {
        val rawResult = executeWriteOp("create") {
            remoteDataSource.createRawPackage(item.toRaw())
        }
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return rawResult.toDomain(warehousesById) ?: item
    }

    override suspend fun update(item: Package): Package {
        val rawResult = executeWriteOp("update") {
            remoteDataSource.updateRawPackage(item.id, item.toRaw())
        }
        val warehousesById = warehouseRepository.getAll().associateBy { it.id }
        return rawResult.toDomain(warehousesById) ?: item
    }

    override suspend fun delete(id: String) {
        executeWriteOp("delete") {
            remoteDataSource.deleteRawPackage(id)
        }
    }

    private suspend fun fetchPackagesFromSource(): List<Package> =
        fetchWithFallback(
            warehouseRepository = warehouseRepository,
            remoteFetch = { remoteDataSource.getRawPackages() },
            localFetch = { localDataSource.getAllPackages() },
            mapToDomain = { raw, map -> raw.toDomain(map) },
            logger = logger
        )
}
