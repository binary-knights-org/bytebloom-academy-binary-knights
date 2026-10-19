package ui

import domain.repository.PackageRepository
import domain.usecase.analytics.ClassifyPackageSectorUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ui.cli.PackageClassificationCli
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class PackageClassificationCliTest {

    private val packageRepository = mockk<PackageRepository>()
    private val classifyPackageSectorUseCase =
        mockk<ClassifyPackageSectorUseCase>()

    private val cli = PackageClassificationCli(
        packageRepository,
        classifyPackageSectorUseCase
    )

    @Test
    fun `should reject invalid latitude`() = runTest {
        val output = captureOutput("invalid\n")

        assertEquals(
            true,
            output.contains("Invalid input. Please enter a valid number.")
        )
    }

    @Test
    fun `should reject invalid longitude`() = runTest {
        val output = captureOutput("31.5\ninvalid\n")

        assertEquals(
            true,
            output.contains("Invalid input. Please enter a valid number.")
        )
    }

    @Test
    fun `should reject invalid package weight`() = runTest {
        val output = captureOutput("31.5\n35.1\ninvalid\n")

        assertEquals(
            true,
            output.contains("Invalid input. Please enter a valid number.")
        )
    }

    @Test
    fun `should report classification failure`() = runTest {
        coEvery { packageRepository.getAll() } returns emptyList()

        coEvery {
            classifyPackageSectorUseCase(
                latitude = any(),
                longitude = any(),
                weight = any(),
                historicalPackages = any()
            )
        } returns Result.failure(IllegalStateException("Classification failed"))

        val output = captureOutput("31.5\n35.1\n5.0\n")

        assertTrue(
            output.contains("Classification failed")
        )
    }

    private suspend fun captureOutput(input: String): String {
        val originalIn = System.`in`
        val originalOut = System.out
        val output = ByteArrayOutputStream()

        return try {
            System.setIn(ByteArrayInputStream(input.toByteArray()))
            System.setOut(PrintStream(output))

            cli.run()

            output.toString()
        } finally {
            System.setIn(originalIn)
            System.setOut(originalOut)
        }
    }
}
