package ui

import domain.model.Warehouse
import domain.repository.WarehouseRepository
import domain.usecase.routing.FindOptimalPathUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class RunRouteCalculationTest {

    private val warehouseRepository = mockk<WarehouseRepository>()
    private val findOptimalPathUseCase = mockk<FindOptimalPathUseCase>()

    @Test
    fun `should report empty origin`() = runTest {
        val output = captureOutput("\n")

        assertEquals(true, output.contains("Origin warehouse ID cannot be empty."))
    }

    @Test
    fun `should report empty destination`() = runTest {
        val output = captureOutput("WH-001\n\n")

        assertEquals(
            true,
            output.contains("Destination warehouse ID cannot be empty.")
        )
    }

    @Test
    fun `should report missing origin warehouse`() = runTest {
        coEvery { warehouseRepository.getById("WH-999") } returns null

        val output = captureOutput("WH-999\nWH-002\n")

        assertEquals(true, output.contains("Origin warehouse not found."))
    }

    @Test
    fun `should report missing destination warehouse`() = runTest {
        val origin = mockk<Warehouse>(relaxed = true)

        coEvery { warehouseRepository.getById("WH-001") } returns origin
        coEvery { warehouseRepository.getById("WH-999") } returns null

        val output = captureOutput("WH-001\nWH-999\n")

        assertEquals(true, output.contains("Destination warehouse not found."))
    }

    private suspend fun captureOutput(input: String): String {
        val originalIn = System.`in`
        val originalOut = System.out
        val output = ByteArrayOutputStream()

        return try {
            System.setIn(ByteArrayInputStream(input.toByteArray()))
            System.setOut(PrintStream(output))

            runRouteCalculation(
                warehouseRepository,
                findOptimalPathUseCase
            )

            output.toString()
        } finally {
            System.setIn(originalIn)
            System.setOut(originalOut)
        }
    }
}
