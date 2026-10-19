package ui

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import ui.cli.withLoadingIndicator

class WithLoadingIndicatorTest {

    @Test
    fun `should return operation result`() = runTest {
        val result = withLoadingIndicator("Loading") { stopLoading ->
            stopLoading()
            "Success"
        }

        assertEquals("Success", result)
    }

    @Test
    fun `should propagate operation exception`() = runTest {
        val exception = assertThrows(IllegalStateException::class.java) {
            kotlinx.coroutines.runBlocking {
                withLoadingIndicator("Loading") { stopLoading ->
                    stopLoading()
                    error("Operation failed")
                }
            }
        }

        assertEquals("Operation failed", exception.message)
    }

    @Test
    fun `should return null when operation returns null`() = runTest {
        val result = withLoadingIndicator<String?>("Loading") { stopLoading ->
            stopLoading()
            null
        }

        assertEquals(null, result)
    }
}
