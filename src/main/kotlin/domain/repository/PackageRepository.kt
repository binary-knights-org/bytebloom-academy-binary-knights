package domain.repository

import data.dataholder.PackageRaw

interface PackageRepository {
    fun getPackages(): List<PackageRaw>
}
