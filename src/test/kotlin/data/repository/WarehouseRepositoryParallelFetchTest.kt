package data.repository

import com.google.common.truth.Truth.assertThat
import data.local.dataholder.PackageRaw
import data.local.dataholder.RouteRaw
import data.local.dataholder.VehicleRaw
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

class WarehouseRepositoryParallelFetchTest : WarehouseRepositoryBaseTest() {

    @Test
    fun `given three independent remote calls when fetching then they run in parallel`() = runTest {
        // Given: each call waits until all three have started, so sequential execution would hang
        val arrived = AtomicInteger()
        val allArrived = CompletableDeferred<Unit>()

        suspend fun arriveAndWait() {
            if (arrived.incrementAndGet() == 3) allArrived.complete(Unit)
            allArrived.await()
        }

        stubRemoteWarehouses()
        coEvery { remotePackage.getRawPackages() } coAnswers {
            arriveAndWait()
            emptyList<PackageRaw>()
        }
        coEvery { remoteVehicle.getRawVehicles() } coAnswers {
            arriveAndWait()
            emptyList<VehicleRaw>()
        }
        coEvery { remoteRoute.getRawRoutes() } coAnswers {
            arriveAndWait()
            emptyList<RouteRaw>()
        }

        // When
        val result = withTimeout(5.seconds) { repository.getAll() }

        // Then
        assertThat(result).hasSize(1)
    }

    @Test
    fun `given remote calls succeed when fetching then returns the warehouse`() = runTest {
        // Given
        stubSuccessfulRemote()

        // When
        val result = withTimeout(5.seconds) { repository.getAll() }

        // Then
        assertThat(result.map { it.id }).containsExactly("WH-1")
    }

    @Test
    fun `given remote calls succeed when fetching then local fallback is not used`() = runTest {
        // Given
        stubSuccessfulRemote()

        // When
        withTimeout(5.seconds) { repository.getAll() }

        // Then
        coVerify(exactly = 0) { localWarehouse.getAllWarehouses() }
    }

    @Test
    fun `given warehouses already fetched when fetching again then remote is called once`() = runTest  {
        // Given
        stubSuccessfulRemote()

        // When
        withTimeout(5.seconds) {
            repository.getAll()
            repository.getAll()
        }

        // Then
        coVerify(exactly = 1) { remoteWarehouse.getRawWarehouses() }
    }

    private fun stubSuccessfulRemote() {
        stubRemoteWarehouses()
        coEvery { remotePackage.getRawPackages() } returns emptyList<PackageRaw>()
        coEvery { remoteVehicle.getRawVehicles() } returns emptyList<VehicleRaw>()
        coEvery { remoteRoute.getRawRoutes() } returns emptyList<RouteRaw>()
    }
}
