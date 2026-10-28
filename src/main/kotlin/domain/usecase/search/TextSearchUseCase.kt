package domain.usecase.search

import domain.service.TelemetryQueryService
import domain.service.SearchState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class TextSearchUseCase<T>(
    loadAll: suspend () -> List<T>,
    matches: (item: T, query: String) -> Boolean
) {
    private val service = TelemetryQueryService<String, T>(
        isSearchable = { it.isNotEmpty()  }
    ) { query ->
        loadAll().filter { matches(it, query) }
    }

    operator fun invoke(queries: Flow<String>): Flow<SearchState<T>> =
        service.queryStream(queries.map { it.trim() })
}

internal fun matchesAny(query: String, vararg fields: String): Boolean =
    fields.any { it.contains(query, ignoreCase = true) }
