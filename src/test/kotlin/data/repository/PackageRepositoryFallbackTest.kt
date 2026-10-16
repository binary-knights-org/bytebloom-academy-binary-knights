package data.repository

import com.google.common.truth.Truth.assertThat
import data.exception.NetworkUnavailableException
import data.local.dataholder.PackageRaw
import data.local.datasource.CsvPackageDataSource
import data.remote.datasource.RemotePackageDataSource
import domain.model.exception.OperationFailedException
import domain.repository.WarehouseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

class PackageRepositoryFallbackTest {

    private val remotePackage = mockk<RemotePackageDataSource>()
    private val localPackage = mockk<CsvPackageDataSource>()
    private val warehouseRepository = mockk<WarehouseRepository>().also {
        coEvery { it.getAll() } returns emptyList()
    }
    private val repository = PackageRepositoryImpl(remotePackage, localPackage, warehouseRepository)

    @Test
    fun `given network error when fetching then falls back to CSV`() = runTest {
        // Given
        coEvery { remotePackage.getRawPackages() } throws NetworkUnavailableException("offline")
        coEvery { localPackage.getAllPackages() } returns emptyList<PackageRaw>()

        // When
        val result = withTimeout(5.seconds) { repository.getAll() }

        // Then
        assertThat(result).isEmpty()
        coVerify(exactly = 1) { localPackage.getAllPackages() }
    }

    @Test
    fun `given non-network error when fetching then error reaches caller`() = runTest {
        // Given
        coEvery { remotePackage.getRawPackages() } throws IllegalStateException("boom")

        // When
        val result = runCatching { withTimeout(5.seconds) { repository.getAll() } }

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(OperationFailedException::class.java)
    }

    @Test
    fun `given non-network error when fetching then CSV is not used`() = runTest {
        // Given
        coEvery { remotePackage.getRawPackages() } throws IllegalStateException("boom")

        // When
        runCatching { withTimeout(5.seconds) { repository.getAll() } }

        // Then
        coVerify(exactly = 0) { localPackage.getAllPackages() }
    }

    @Test
    fun `given running fetch when caller cancels then caller job ends as cancelled`() = runTest {
        // Given
        val started = stubHangingRemote()

        // When
        val job = launch { repository.getAll() }
        withTimeout(5.seconds) { started.await() }
        job.cancel()
        withTimeout(5.seconds) { job.join() }

        // Then
        assertThat(job.isCancelled).isTrue()
    }

    @Test
    fun `given running fetch when caller cancels then CSV is not used`() = runTest {
        // Given
        val started = stubHangingRemote()

        // When
        val job = launch { repository.getAll() }
        withTimeout(5.seconds) { started.await() }
        job.cancel()
        withTimeout(5.seconds) { job.join() }

        // Then
        coVerify(exactly = 0) { localPackage.getAllPackages() }
    }

    private fun stubHangingRemote(): CompletableDeferred<Unit> {
        val started = CompletableDeferred<Unit>()
        coEvery { remotePackage.getRawPackages() } coAnswers {
            started.complete(Unit)
            awaitCancellation()
        }
        return started
    }
}
