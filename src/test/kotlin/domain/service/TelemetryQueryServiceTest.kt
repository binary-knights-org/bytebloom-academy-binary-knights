package domain.service

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TelemetryQueryServiceTest {

    @Test
    fun `queryStream should execute search only once for rapid inputs`() = runTest {
        val searchedQueries = mutableListOf<String>()

        val service = TelemetryQueryService { query ->
            searchedQueries.add(query)
            listOf("Result for $query")
        }

        val queries = flow {
            listOf("T", "TR", "TRK", "TRK-", "TRK-101").forEach { query ->
                emit(query)
                delay(30)
            }
            delay(350)
        }

        val results = service.queryStream(queries).toList()

        assertThat(searchedQueries).containsExactly("TRK-101")
        assertThat(results).containsExactly(listOf("Result for TRK-101"))
    }

    @Test
    fun `queryStream should not start search before debounce window elapses`() = runTest {
        val searchedQueries = mutableListOf<String>()

        val service = TelemetryQueryService { query ->
            searchedQueries.add(query)
            listOf("Result for $query")
        }

        val input = MutableSharedFlow<String>()
        val job = launch { service.queryStream(input).collect { } }
        runCurrent()

        input.emit("TRK-101")

        advanceTimeBy(299)
        assertThat(searchedQueries).isEmpty()

        advanceTimeBy(2)
        runCurrent()
        assertThat(searchedQueries).containsExactly("TRK-101")

        job.cancel()
    }

    @Test
    fun `queryStream should ignore consecutive duplicate queries`() = runTest {
        val searchedQueries = mutableListOf<String>()

        val service = TelemetryQueryService { query ->
            searchedQueries.add(query)
            listOf("Result for $query")
        }

        val queries = flow {
            emit("TRK-101")
            delay(350)

            emit("TRK-101")
            delay(350)

            emit("TRK-102")
            delay(350)
        }

        service.queryStream(queries).toList()

        assertThat(searchedQueries).containsExactly("TRK-101", "TRK-102").inOrder()
    }

    @Test
    fun `queryStream should cancel previous search when a new debounced query arrives`() = runTest {
        val startedQueries = mutableListOf<String>()
        val cancelledQueries = mutableListOf<String>()

        val service = TelemetryQueryService { query ->
            startedQueries.add(query)
            try {
                delay(1_000)
                listOf("Result for $query")
            } catch (e: CancellationException) {
                // Only reached when the search is actually cancelled.
                cancelledQueries.add(query)
                throw e
            }
        }

        val queries = flow {
            emit("TRK-101")
            delay(350)

            emit("TRK-102")
            delay(350)
        }

        val results = service.queryStream(queries).toList()

        assertThat(startedQueries).containsExactly("TRK-101", "TRK-102").inOrder()
        assertThat(cancelledQueries).containsExactly("TRK-101")
        // The canceled search must never deliver a result.
        assertThat(results).containsExactly(listOf("Result for TRK-102"))
    }

    @Test
    fun `queryStream should not search for blank input`() = runTest {
        val searchedQueries = mutableListOf<String>()

        val service = TelemetryQueryService { query ->
            searchedQueries.add(query)
            listOf("Result for $query")
        }

        val queries = flow {
            emit("   ")
            delay(500)
        }

        val results = service.queryStream(queries).toList()

        assertThat(searchedQueries).isEmpty()
        assertThat(results).isEmpty()
    }

    @Test
    fun `queryStream should discard pending query when input is cleared`() = runTest {
        val searchedQueries = mutableListOf<String>()

        val service = TelemetryQueryService { query ->
            searchedQueries.add(query)
            listOf("Result for $query")
        }

        val queries = flow {
            emit("TRK-101")
            delay(100)
            emit("")          // operator cleared the field before to debounce fired
            delay(500)
        }

        service.queryStream(queries).toList()

        assertThat(searchedQueries).isEmpty()
    }

    @Test
    fun `queryStream should survive a failing search and report the error`() = runTest {
        val errors = mutableListOf<Throwable>()

        val service = TelemetryQueryService(onError = { errors.add(it) }) { query ->
            if (query == "bad") error("boom")
            listOf("Result for $query")
        }

        val queries = flow {
            emit("bad")
            delay(500)
            emit("good")
            delay(500)
        }

        val results = service.queryStream(queries).toList()

        assertThat(results).containsExactly(emptyList<String>(),
            listOf("Result for good")).inOrder()
        assertThat(errors).hasSize(1)
        assertThat(errors.single()).hasMessageThat().isEqualTo("boom")
    }
}
