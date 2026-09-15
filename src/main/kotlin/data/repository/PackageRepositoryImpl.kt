package data.repository

import data.datasource.PackageDataSource
import data.mapper.packages.toDomain
import domain.model.Package
import domain.repository.PackageRepository
import domain.repository.WarehouseRepository

class PackageRepositoryImpl(
    private val dataSource: PackageDataSource,
    private val warehouseRepository: WarehouseRepository
) : PackageRepository {

    private var packages: List<Package>? = null

    override suspend fun getAllPackages(): List<Package> {
        packages?.let { return it }
        val warehousesById = warehouseRepository.getAllWarehouses().associateBy { it.id }
        val loadedPackages = dataSource.getRawPackages().mapNotNull { it.toDomain(warehousesById) }

        packages = loadedPackages
        return loadedPackages
    }
}
