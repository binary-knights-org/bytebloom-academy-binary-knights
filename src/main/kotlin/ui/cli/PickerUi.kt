package ui.cli

import domain.service.SearchState

/** Everything the picker screen shows. Pure data: easy to reason about and to test. */
internal data class PickerUi<T>(
    val text: String = "",
    val items: List<T>? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val selected: Int = 0
) {
    fun selectedItem(): T? = items?.getOrNull(selected)
}

internal fun <T> PickerUi<T>.reduce(state: SearchState<T>): PickerUi<T> =
    when (state) {
        SearchState.Idle -> copy(items = null, loading = false, error = null, selected = 0)
        SearchState.Loading -> copy(loading = true, error = null)
        is SearchState.Success -> copy(items = state.items, loading = false, error = null, selected = 0)
        is SearchState.Failure -> copy(
            loading = false,
            error = "Search failed: ${state.error.message ?: "unknown error"}"
        )
    }

internal fun <T> PickerUi<T>.onKey(key: Key): PickerUi<T> =
    when (key) {
        is Key.Character -> withText(text + key.value)
        Key.Backspace -> withText(text.dropLast(1))
        Key.Up -> moveSelection(-1)
        Key.Down -> moveSelection(1)
        else -> this
    }

private fun <T> PickerUi<T>.withText(newText: String): PickerUi<T> =
    if (newText.isBlank()) PickerUi(text = newText) else copy(text = newText)

private fun <T> PickerUi<T>.moveSelection(delta: Int): PickerUi<T> {
    val lastIndex = (items?.size ?: 0) - 1
    return copy(selected = (selected + delta).coerceIn(0, maxOf(lastIndex, 0)))
}
