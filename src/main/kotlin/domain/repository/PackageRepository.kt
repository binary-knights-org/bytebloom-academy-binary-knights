package domain.repository

import domain.model.Package

interface PackageRepository {
   suspend fun getAllPackages(): List<Package>
}
