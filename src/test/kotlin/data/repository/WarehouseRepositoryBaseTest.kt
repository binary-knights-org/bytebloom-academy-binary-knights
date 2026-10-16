package data.repository

import data.local.dataholder.PackageRaw
import data.local.dataholder.RouteRaw
import data.local.dataholder.VehicleRaw
import data.local.dataholder.WarehouseRaw
import data.local.datasource.CsvPackageDataSource
import data.local.datasource.CsvRouteDataSource
import data.local.datasource.CsvVehicleDataSource
import data.local.datasource.CsvWarehouseDataSource
import data.remote.datasource.RemotePackageDataSource
import data.remote.datasource.RemoteRouteDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.remote.datasource.RemoteWarehouseDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation

class HangingCall {
    val started = CompletableDeferred<Unit>()
    val cancelled = CompletableDeferred<Unit>()

    suspend fun hang(): Nothing {
        try {
            started.complete(Unit)
            awaitCancellation()
        } catch (e: CancellationException) {
            cancelled.complete(Unit)
            throw e
        }
    }
}

abstract class WarehouseRepositoryBaseTest {

    protected val remoteWarehouse = mockk<RemoteWarehouseDataSource>()
    protected val remotePackage = mockk<RemotePackageDataSource>()
    protected val remoteVehicle = mockk<RemoteVehicleDataSource>()
    protected val remoteRoute = mockk<RemoteRouteDataSource>()

    protected val localWarehouse = mockk<CsvWarehouseDataSource>()
    protected val localPackage = mockk<CsvPackageDataSource>()
    protected val localVehicle = mockk<CsvVehicleDataSource>()
    protected val localRoute = mockk<CsvRouteDataSource>()

    protected val repository = WarehouseRepositoryImpl(
        RemoteDataSources(remoteWarehouse, remotePackage, remoteVehicle, remoteRoute),
        LocalDataSources(localWarehouse, localPackage, localVehicle, localRoute)
    )

    protected val warehouseRaw = WarehouseRaw("WH-1", "HUB", "NORTH", 0.0, 0.0)

    protected fun stubRemoteWarehouses() {
        coEvery { remoteWarehouse.getRawWarehouses() } returns listOf(warehouseRaw)
    }

    protected fun stubEmptyLocalData() {
        coEvery { localWarehouse.getAllWarehouses() } returns listOf(warehouseRaw)
        coEvery { localPackage.getAllPackages() } returns emptyList<PackageRaw>()
        coEvery { localVehicle.getAllVehicles() } returns emptyList<VehicleRaw>()
        coEvery { localRoute.getAllRoutes() } returns emptyList<RouteRaw>()
    }
}
