package data.repository

import com.google.common.truth.Truth.assertThat
import data.exception.NetworkUnavailableException
import domain.model.exception.OperationFailedException
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

class WarehouseRepositoryFallbackTest : WarehouseRepositoryBaseTest() {

    // ---------- network error: fallback runs ----------

    @Test
    fun `given network error when fetching then falls back to local data`() = runTest {
        // Given
        stubRemoteWarehouses()
        coEvery { remotePackage.getRawPackages() } throws NetworkUnavailableException("offline")
        coEvery { remoteVehicle.getRawVehicles() } coAnswers { awaitCancellation() }
        coEvery { remoteRoute.getRawRoutes() } coAnswers { awaitCancellation() }
        stubEmptyLocalData()

        // When
        val result = withTimeout(5.seconds) { repository.getAll() }

        // Then
        assertThat(result).hasSize(1)
        coVerify(exactly = 1) { localWarehouse.getAllWarehouses() }
    }

    @Test
    fun `given network error when fetching then remote siblings are cancelled before fallback`() =
        runTest {
            // Given
            val vehicles = HangingCall()
            stubRemoteWarehouses()
            coEvery { remotePackage.getRawPackages() } coAnswers {
                vehicles.started.await()
                throw NetworkUnavailableException("offline")
            }
            coEvery { remoteVehicle.getRawVehicles() } coAnswers { vehicles.hang() }
            coEvery { remoteRoute.getRawRoutes() } coAnswers { awaitCancellation() }
            stubEmptyLocalData()

            // When
            withTimeout(5.seconds) { repository.getAll() }

            // Then
            withTimeout(5.seconds) { vehicles.cancelled.await() }
        }

    // ---------- other errors: no fallback, error propagates ----------

    @Test
    fun `given non-network error when fetching then error reaches caller`() = runTest  {
        // Given
        stubOtherError()

        // When
        val result = runCatching { withTimeout(5.seconds) { repository.getAll() } }

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(OperationFailedException::class.java)
    }

    @Test
    fun `given non-network error when fetching then local fallback is not used`() = runTest  {
        // Given
        stubOtherError()

        // When
        runCatching { withTimeout(5.seconds) { repository.getAll() } }

        // Then
        coVerify(exactly = 0) { localWarehouse.getAllWarehouses() }
        coVerify(exactly = 0) { localPackage.getAllPackages() }
        coVerify(exactly = 0) { localVehicle.getAllVehicles() }
        coVerify(exactly = 0) { localRoute.getAllRoutes() }
    }

    // ---------- cancellation: no fallback ----------

    @Test
    fun `given running fetch when caller cancels then local fallback is not used`() = runTest  {
        // Given
        val packages = HangingCall()
        stubRemoteWarehouses()
        coEvery { remotePackage.getRawPackages() } coAnswers { packages.hang() }
        coEvery { remoteVehicle.getRawVehicles() } coAnswers { awaitCancellation() }
        coEvery { remoteRoute.getRawRoutes() } coAnswers { awaitCancellation() }

        // When
        val job = launch { repository.getAll() }
        withTimeout(5.seconds) { packages.started.await() }
        job.cancel()
        withTimeout(5.seconds) { job.join() }

        // Then
        coVerify(exactly = 0) { localWarehouse.getAllWarehouses() }
    }

    private fun stubOtherError() {
        stubRemoteWarehouses()
        coEvery { remotePackage.getRawPackages() } throws IllegalStateException("boom")
        coEvery { remoteVehicle.getRawVehicles() } coAnswers { awaitCancellation() }
        coEvery { remoteRoute.getRawRoutes() } coAnswers { awaitCancellation() }
    }
}
