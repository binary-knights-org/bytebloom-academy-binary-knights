package data.repository

import com.google.common.truth.Truth.assertThat
import domain.model.exception.OperationFailedException
import io.mockk.coEvery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

class WarehouseRepositoryCancellationTest : WarehouseRepositoryBaseTest() {

    private val packages = HangingCall()
    private val vehicles = HangingCall()
    private val routes = HangingCall()

    // ---------- child failure ----------

    @Test
    fun `given packages call fails when fetching then error reaches caller as OperationFailedException`() =
        runTest {
            // Given
            stubFailingPackages()

            // When
            val result = runCatching { withTimeout(5.seconds) { repository.getAll() } }

            // Then
            assertThat(result.exceptionOrNull()).isInstanceOf(OperationFailedException::class.java)
        }

    @Test
    fun `given packages call fails when fetching then vehicles sibling is cancelled`() =
        runTest {
        // Given
        stubFailingPackages()

        // When
        runCatching { withTimeout(5.seconds) { repository.getAll() } }

        // Then
        withTimeout(5.seconds) { vehicles.cancelled.await() }
    }

    @Test
    fun `given packages call fails when fetching then routes sibling is cancelled`() =
        runTest {
        // Given
        stubFailingPackages()

        // When
        runCatching { withTimeout(5.seconds) { repository.getAll() } }

        // Then
        withTimeout(5.seconds) { routes.cancelled.await() }
    }


    @Test
    fun `given running fetch when caller cancels then all child calls are cancelled`() =
        runTest {
        // Given
        stubHangingCalls()

        // When
        cancelRunningGetAll()

        // Then
        withTimeout(5.seconds) {
            packages.cancelled.await()
            vehicles.cancelled.await()
            routes.cancelled.await()
        }
    }

    @Test
    fun `given running fetch when caller cancels then caller job ends as cancelled`() =
        runTest {
        // Given
        stubHangingCalls()

        // When
        val job = cancelRunningGetAll()

        // Then
        assertThat(job.isCancelled).isTrue()
    }


    private fun stubFailingPackages() {
        stubRemoteWarehouses()
        coEvery { remotePackage.getRawPackages() } coAnswers {
            vehicles.started.await()
            routes.started.await()
            error("boom")
        }
        coEvery { remoteVehicle.getRawVehicles() } coAnswers { vehicles.hang() }
        coEvery { remoteRoute.getRawRoutes() } coAnswers { routes.hang() }
    }

    private fun stubHangingCalls() {
        stubRemoteWarehouses()
        coEvery { remotePackage.getRawPackages() } coAnswers { packages.hang() }
        coEvery { remoteVehicle.getRawVehicles() } coAnswers { vehicles.hang() }
        coEvery { remoteRoute.getRawRoutes() } coAnswers { routes.hang() }
    }

    private suspend fun CoroutineScope.cancelRunningGetAll(): Job {
        val job = launch { repository.getAll() }
        withTimeout(5.seconds) {
            packages.started.await()
            vehicles.started.await()
            routes.started.await()
        }
        job.cancel()
        withTimeout(5.seconds) { job.join() }
        return job
    }
}
