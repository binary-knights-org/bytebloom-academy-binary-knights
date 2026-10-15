package data.local.datasource

import data.local.dataholder.PackageRaw

interface CsvPackageDataSource {
    suspend fun getAllPackages(): List<PackageRaw>
}
