package data.local.datasource

import data.local.dataholder.PackageRaw

interface CsvPackageDataSource {
    fun getAllPackages(): List<PackageRaw>
}
