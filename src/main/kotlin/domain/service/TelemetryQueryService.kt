package domain.service

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TelemetryQueryService(
    private val onError: (Throwable) -> Unit = {},
    private val search: suspend (String) -> List<String>
) {
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun queryStream(queries: Flow<String>): Flow<List<String>> =
        queries
            .map { it.trim() }
            .debounce(DEBOUNCE_MS)
            .filter { it.isNotEmpty() }
            .distinctUntilChanged()
            .flatMapLatest { query ->
                flow { emit(search(query)) }
                    .catch { e ->
                        if (e is CancellationException) throw e
                        onError(e)
                        emit(emptyList())
                    }
            }

    companion object {
        const val DEBOUNCE_MS = 300L
    }
}
