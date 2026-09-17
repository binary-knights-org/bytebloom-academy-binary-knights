package data.remote.supabase.datasource

import data.dataholder.PackageRaw

interface PackageDataSource {
   suspend fun getRawPackages(): List<PackageRaw>
   suspend fun createRawPackage(pkg: PackageRaw): Boolean
   suspend fun updateRawPackage(id: String, pkg: PackageRaw): Boolean
   suspend fun deleteRawPackage(id: String): Boolean
}
