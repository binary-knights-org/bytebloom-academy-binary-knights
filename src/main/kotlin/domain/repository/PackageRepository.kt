package domain.repository

import domain.model.Package

interface PackageRepository {
    fun getAllPackages(): List<Package>
}
