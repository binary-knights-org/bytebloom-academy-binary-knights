package data

import com.google.common.truth.Truth.assertThat
import data.local.dataholder.WarehouseRaw
import data.local.datasource.CsvPackageDataSource
import data.local.datasource.CsvRouteDataSource
import data.local.datasource.CsvVehicleDataSource
import data.local.datasource.CsvWarehouseDataSource
import data.remote.datasource.RemotePackageDataSource
import data.remote.datasource.RemoteRouteDataSource
import data.remote.datasource.RemoteVehicleDataSource
import data.remote.datasource.RemoteWarehouseDataSource
import data.repository.LocalDataSources
import data.repository.RemoteDataSources
import data.repository.WarehouseRepositoryImpl
import domain.model.exception.OperationFailedException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Test

class WarehouseRepositoryCancellationTest {

    private val remoteWarehouse = mockk<RemoteWarehouseDataSource>()
    private val remotePackage = mockk<RemotePackageDataSource>()
    private val remoteVehicle = mockk<RemoteVehicleDataSource>()
    private val remoteRoute = mockk<RemoteRouteDataSource>()

    private val repository = WarehouseRepositoryImpl(
        RemoteDataSources(remoteWarehouse, remotePackage, remoteVehicle, remoteRoute),
        LocalDataSources(
            mockk<CsvWarehouseDataSource>(),
            mockk<CsvPackageDataSource>(),
            mockk<CsvVehicleDataSource>(),
            mockk<CsvRouteDataSource>()
        )
    )

    @Test
    fun `child failure cancels sibling coroutines and does not leak them`() = runBlocking {
        val siblingStarted = CompletableDeferred<Unit>()
        val siblingCancelled = CompletableDeferred<Unit>()

        coEvery { remoteWarehouse.getRawWarehouses() } returns listOf(
            WarehouseRaw("WH-1", "HUB", "NORTH", 0.0, 0.0)
        )
        coEvery { remotePackage.getRawPackages() } coAnswers {
            siblingStarted.await()
            throw IllegalStateException("boom")
        }
        coEvery { remoteVehicle.getRawVehicles() } coAnswers {
            try {
                siblingStarted.complete(Unit)
                awaitCancellation()
            } catch (e: CancellationException) {
                siblingCancelled.complete(Unit)
                throw e
            }
        }
        coEvery { remoteRoute.getRawRoutes() } coAnswers { awaitCancellation() }

        val result = runCatching { withTimeout(5_000) { repository.getAll() } }

        assertThat(result.exceptionOrNull()).isInstanceOf(OperationFailedException::class.java)
        withTimeout(5_000) { siblingCancelled.await() }
    }
}