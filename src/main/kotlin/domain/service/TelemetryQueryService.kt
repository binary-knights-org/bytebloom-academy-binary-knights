package domain.service

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf


class TelemetryQueryService<Q, T>(
    private val isSearchable: (Q) -> Boolean,
    private val search: suspend (Q) -> List<T>
) {
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun queryStream(queries: Flow<Q>): Flow<SearchState<T>> =
        queries
            .debounce(DEBOUNCE_MS)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (isSearchable(query)) execute(query) else flowOf(SearchState.Idle)
            }

    private fun execute(query: Q): Flow<SearchState<T>> =
        flow<SearchState<T>> {
            emit(SearchState.Loading)
            emit(SearchState.Success(search(query)))
        }.catch { error ->
            if (error is CancellationException) throw error
            emit(SearchState.Failure(error))
        }

    companion object {
        const val DEBOUNCE_MS = 300L
    }
}
