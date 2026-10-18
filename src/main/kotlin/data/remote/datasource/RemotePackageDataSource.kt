package data.remote.datasource

import data.local.dataholder.PackageRaw

interface RemotePackageDataSource {
   suspend fun getRawPackages(): List<PackageRaw>
   suspend fun createRawPackage(pkg: PackageRaw): PackageRaw
   suspend fun updateRawPackage(id: String, pkg: PackageRaw): PackageRaw
   suspend fun deleteRawPackage(id: String): Boolean
}
