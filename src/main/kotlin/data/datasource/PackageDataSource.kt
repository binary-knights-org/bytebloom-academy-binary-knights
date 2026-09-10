package data.datasource

import data.dataholder.PackageRaw

interface PackageDataSource {
    fun getRawPackages(): List<PackageRaw>
}
