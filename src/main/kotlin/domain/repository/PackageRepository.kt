package domain.repository

import domain.model.Package

interface PackageRepository : BaseRepository<Package, String> {

   suspend fun getAllPackages(): List<Package>
   suspend fun getPackageById(id: String): Package?
   suspend fun createPackage(pkg: Package): Boolean
   suspend fun deletePackage(id: String): Boolean
   suspend fun updatePackage(pkg: Package): Boolean
}
