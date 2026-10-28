package domain.service

sealed interface SearchState<out T> {
    data object Idle : SearchState<Nothing>

    data object Loading : SearchState<Nothing>

    data class Success<T>(val items: List<T>) : SearchState<T>

    data class Failure(val error: Throwable) : SearchState<Nothing>
}
