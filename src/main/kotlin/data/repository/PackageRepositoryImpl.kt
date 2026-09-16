package data.repository

import data.datasource.PackageDataSource
import data.mapper.packages.toDomain
import data.mapper.packages.toRaw
import domain.model.Package
import domain.repository.PackageRepository
import domain.repository.WarehouseRepository

class PackageRepositoryImpl(
    private val dataSource: PackageDataSource,
    private val warehouseRepository: WarehouseRepository
) : PackageRepository {

    private var packages: List<Package>? = null

    override suspend fun getAll(): List<Package> =
        packages ?: fetchPackagesFromSource().also { packages = it }

    override suspend fun getById(id: String): Package? =
        getAll().find { it.id == id }

    override suspend fun create(item: Package): Boolean =
        dataSource.createRawPackage(item.toRaw()).also { isSuccess ->
            if (isSuccess) packages = null
        }

    override suspend fun update(item: Package): Boolean =
        dataSource.updateRawPackage(item.id, item.toRaw()).also { isSuccess ->
            if (isSuccess) packages = null
        }

    override suspend fun delete(id: String): Boolean =
        dataSource.deleteRawPackage(id).also { isSuccess ->
            if (isSuccess) packages = null
        }

    private suspend fun fetchPackagesFromSource(): List<Package> =
        warehouseRepository.getAll()
            .associateBy { it.id }
            .let { warehousesById ->
                dataSource.getRawPackages().mapNotNull { it.toDomain(warehousesById) }
            }
}
