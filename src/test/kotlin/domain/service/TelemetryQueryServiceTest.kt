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

    private fun textService(search: suspend (String) -> List<String>) =
        TelemetryQueryService<String, String>(isSearchable = { it.isNotBlank() }, search = search)

    @Test
    fun `rapid keystrokes trigger a single search`() = runTest {
        val searched = mutableListOf<String>()
        val service = textService { query ->
            searched.add(query)
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

        assertThat(searched).containsExactly("TRK-101")
        assertThat(results)
            .containsExactly(
                SearchState.Loading,
                SearchState.Success(listOf("Result for TRK-101"))
            )
            .inOrder()
    }

    @Test
    fun `search does not start before the debounce window elapses`() = runTest {
        val searched = mutableListOf<String>()
        val service = textService { query ->
            searched.add(query)
            listOf(query)
        }

        val input = MutableSharedFlow<String>()
        val job = launch { service.queryStream(input).collect { } }
        runCurrent()

        input.emit("TRK-101")

        advanceTimeBy(299)
        assertThat(searched).isEmpty()

        advanceTimeBy(2)
        runCurrent()
        assertThat(searched).containsExactly("TRK-101")

        job.cancel()
    }

    @Test
    fun `consecutive duplicate queries are searched once`() = runTest {
        val searched = mutableListOf<String>()
        val service = textService { query ->
            searched.add(query)
            listOf(query)
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

        assertThat(searched).containsExactly("TRK-101", "TRK-102").inOrder()
    }

    @Test
    fun `new query cancels the in-flight search`() = runTest {
        val started = mutableListOf<String>()
        val cancelled = mutableListOf<String>()
        val service = textService { query ->
            started.add(query)
            try {
                delay(1_000)
                listOf("Result for $query")
            } catch (e: CancellationException) {
                cancelled.add(query)
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

        assertThat(started).containsExactly("TRK-101", "TRK-102").inOrder()
        assertThat(cancelled).containsExactly("TRK-101")
        assertThat(results.filterIsInstance<SearchState.Success<*>>().map { it.items })
            .containsExactly(listOf("Result for TRK-102"))
    }

    @Test
    fun `clearing the input cancels the in-flight search and emits idle`() = runTest {
        val cancelled = mutableListOf<String>()
        val service = textService { query ->
            try {
                delay(1_000)
                listOf("Result for $query")
            } catch (e: CancellationException) {
                cancelled.add(query)
                throw e
            }
        }

        val queries = flow {
            emit("TRK-101")
            delay(500)
            emit("")
            delay(500)
        }

        val results = service.queryStream(queries).toList()

        assertThat(cancelled).containsExactly("TRK-101")
        assertThat(results).containsExactly(SearchState.Loading, SearchState.Idle).inOrder()
    }

    @Test
    fun `blank input is not searched`() = runTest {
        val searched = mutableListOf<String>()
        val service = textService { query ->
            searched.add(query)
            listOf(query)
        }

        val results = service.queryStream(
            flow {
                emit("   ")
                delay(500)
            }
        ).toList()

        assertThat(searched).isEmpty()
        assertThat(results).containsExactly(SearchState.Idle)
    }

    @Test
    fun `query cleared before the debounce fires is discarded`() = runTest {
        val searched = mutableListOf<String>()
        val service = textService { query ->
            searched.add(query)
            listOf(query)
        }

        val results = service.queryStream(
            flow {
                emit("TRK-101")
                delay(100)
                emit("")
                delay(500)
            }
        ).toList()

        assertThat(searched).isEmpty()
        assertThat(results).containsExactly(SearchState.Idle)
    }

    @Test
    fun `a failing search emits failure and the stream keeps working`() = runTest {
        val service = textService { query ->
            if (query == "bad") error("boom")
            listOf("Result for $query")
        }

        val results = service.queryStream(
            flow {
                emit("bad")
                delay(500)
                emit("good")
                delay(500)
            }
        ).toList()

        assertThat(results).hasSize(4)
        assertThat(results[0]).isEqualTo(SearchState.Loading)
        assertThat((results[1] as SearchState.Failure).error).hasMessageThat().isEqualTo("boom")
        assertThat(results[2]).isEqualTo(SearchState.Loading)
        assertThat(results[3]).isEqualTo(SearchState.Success(listOf("Result for good")))
    }

    @Test
    fun `works with non-text queries and skips incomplete ones`() = runTest {
        val searched = mutableListOf<Int>()
        val service = TelemetryQueryService<Int, Int>(isSearchable = { it > 0 }) { number ->
            searched.add(number)
            listOf(number * 2)
        }

        val results = service.queryStream(
            flow {
                emit(0)
                delay(500)
                emit(5)
                delay(500)
            }
        ).toList()

        assertThat(searched).containsExactly(5)
        assertThat(results)
            .containsExactly(
                SearchState.Idle,
                SearchState.Loading,
                SearchState.Success(listOf(10))
            )
            .inOrder()
    }
}
