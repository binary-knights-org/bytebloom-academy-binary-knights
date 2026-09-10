package data.repository

import data.datasource.PackageDataSource
import data.mapper.toDomain
import domain.model.Package
import domain.repository.PackageRepository
import domain.repository.WarehouseRepository

class PackageRepositoryImpl(
    private val dataSource: PackageDataSource,
    private val warehouseRepository: WarehouseRepository
) : PackageRepository {

    override fun getAllPackages(): List<Package> {
        val warehousesById = warehouseRepository.getAllWarehouses().associateBy { it.id }

        return dataSource.getRawPackages().mapNotNull { it.toDomain(warehousesById) }
    }
}
