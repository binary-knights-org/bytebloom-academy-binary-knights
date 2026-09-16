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

    override suspend fun getPackageById(id: String): Package? {
        return getAllPackages().find { it.id == id }
    }

    override suspend fun createPackage(pkg: Package): Boolean {
        val currentPackages = getAllPackages().toMutableList()
        if (currentPackages.any { it.id == pkg.id }) {
            return false
        }
        currentPackages.add(pkg)
        packages = currentPackages
        return true
    }

    override suspend fun deletePackage(id: String): Boolean {
        val currentPackages = getAllPackages().toMutableList()
        val removed = currentPackages.removeIf { it.id == id }
        if (removed) {
            packages = currentPackages
        }
        return removed
    }

    override suspend fun updatePackage(pkg: Package): Boolean {
        val currentPackages = getAllPackages().toMutableList()
        val index = currentPackages.indexOfFirst { it.id == pkg.id }
        if (index == -1) {
            return false
        }
        currentPackages[index] = pkg
        packages = currentPackages
        return true
    }
}
