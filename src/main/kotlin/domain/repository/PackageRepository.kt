package domain.repository

import domain.model.Package

interface PackageRepository : BaseRepository<Package, String> {

   suspend fun getAllPackages(): List<Package>
}
