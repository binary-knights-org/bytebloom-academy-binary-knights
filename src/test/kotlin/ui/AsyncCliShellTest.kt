package ui

import domain.repository.WarehouseRepository
import domain.usecase.routing.FindOptimalPathUseCase
import domain.usecase.telemetry.TrackActiveFleetUseCase
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class AsyncCliShellTest {

    private val warehouseRepository = mockk<WarehouseRepository>()
    private val findOptimalPathUseCase = mockk<FindOptimalPathUseCase>()
    private val trackActiveFleetUseCase = mockk<TrackActiveFleetUseCase>()
    private val packageClassificationCli = mockk<PackageClassificationCli>(
        relaxed = true
    )

    private val shell = AsyncCliShell(
        warehouseRepository,
        findOptimalPathUseCase,
        trackActiveFleetUseCase,
        packageClassificationCli
    )

    @Test
    fun `should exit when option zero is selected`() = runTest {
        val output = captureOutput("0\n")

        assertEquals(true, output.contains("Goodbye!"))
    }

    @Test
    fun `should exit when input ends`() = runTest {
        val output = captureOutput("")

        assertEquals(true, output.contains("Goodbye!"))
    }

    @Test
    fun `should report invalid menu option`() = runTest {
        val output = captureOutput("9\n0\n")

        assertEquals(
            true,
            output.contains("Invalid option. Please choose 0, 1, 2, or 3.")
        )
    }

    private suspend fun captureOutput(input: String): String {
        val originalIn = System.`in`
        val originalOut = System.out
        val output = ByteArrayOutputStream()

        return try {
            System.setIn(ByteArrayInputStream(input.toByteArray()))
            System.setOut(PrintStream(output))

            shell.run()

            output.toString()
        } finally {
            System.setIn(originalIn)
            System.setOut(originalOut)
        }
    }
}
