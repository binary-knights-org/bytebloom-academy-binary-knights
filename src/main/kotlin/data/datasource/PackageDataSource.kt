package data.datasource

import data.dataholder.PackageRaw

interface PackageDataSource {
   suspend fun getRawPackages(): List<PackageRaw>
}
