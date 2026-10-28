package ui.cli

import domain.service.SearchState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import ui.cli.Ansi.BLUE
import ui.cli.Ansi.RED
import ui.cli.Ansi.RESET
import ui.cli.Ansi.YELLOW

private const val BACK_COMMAND = ":back"
private const val SELECT_PREFIX = "#"

internal class LivePicker {

    suspend fun <T> pick(
        title: String,
        search: (Flow<String>) -> Flow<SearchState<T>>,
        describe: (T) -> String
    ): T? {
        val terminal = openTerminal()

        return if (terminal == null) {
            pickByLine(title, search, describe)
        } else {
            terminal.use { pickByKeystroke(it, title, search, describe) }
        }
    }

    private fun openTerminal(): Terminal? =
        runCatching { TerminalBuilder.builder().system(true).dumb(true).build() }
            .getOrNull()
            ?.let { terminal ->
                if (terminal.type.startsWith(Terminal.TYPE_DUMB)) {
                    terminal.close()
                    null
                } else {
                    terminal
                }
            }
    private suspend fun <T> pickByKeystroke(
        terminal: Terminal,
        title: String,
        search: (Flow<String>) -> Flow<SearchState<T>>,
        describe: (T) -> String
    ): T? = coroutineScope {
        val ui = MutableStateFlow(PickerUi<T>())
        val savedAttributes = terminal.enterRawMode()

        val renderer = launch { ui.collect { terminal.render(title, it, describe) } }
        val searcher = launch {
            search(ui.map { it.text }.distinctUntilChanged()).collect { state ->
                ui.update { current -> if (current.text.isBlank()) current else current.reduce(state) }
            }
        }

        try {
            selectLoop(terminal, ui)
        } finally {
            renderer.cancel()
            searcher.cancel()
            terminal.restore(savedAttributes)
        }
    }

    private suspend fun <T> selectLoop(
        terminal: Terminal,
        ui: MutableStateFlow<PickerUi<T>>
    ): T? {
        while (true) {
            when (val key = terminal.reader().readKey()) {
                Key.Cancel -> return null
                Key.Enter -> ui.value.selectedItem()?.let { return it }
                else -> ui.update { it.onKey(key) }
            }
        }
    }

    private suspend fun <T> pickByLine(
        title: String,
        search: (Flow<String>) -> Flow<SearchState<T>>,
        describe: (T) -> String
    ): T? = coroutineScope {
        println("$BLUE$title$RESET")
        println("Type a query and press Enter. Use $SELECT_PREFIX<number> to select, $BACK_COMMAND to cancel.")

        val queries = Channel<String>(Channel.UNLIMITED)
        var latest: List<T> = emptyList()

        val printer = launch {
            search(queries.receiveAsFlow()).collect { state ->
                latest = (state as? SearchState.Success)?.items.orEmpty()
                printLineState(state, describe)
            }
        }

        try {
            readLineSelection(queries) { latest }
        } finally {
            printer.cancel()
        }
    }

    private suspend fun <T> readLineSelection(
        queries: Channel<String>,
        latest: () -> List<T>
    ): T? {
        while (true) {
            val line = readInputLine()
            val number = line?.takeIf { it.startsWith(SELECT_PREFIX) }?.drop(1)?.toIntOrNull()

            when {
                line == null || line == BACK_COMMAND -> return null
                number != null -> latest().getOrNull(number - 1)?.let { return it }
                    ?: println("${YELLOW}No result #$number.$RESET")
                else -> queries.send(line)
            }
        }
    }

    private fun <T> printLineState(state: SearchState<T>, describe: (T) -> String) {
        when (state) {
            SearchState.Idle, SearchState.Loading -> Unit
            is SearchState.Failure ->
                println("${RED}Search failed: ${state.error.message ?: "unknown error"}$RESET")
            is SearchState.Success -> {
                if (state.items.isEmpty()) {
                    println("${YELLOW}No results found.$RESET")
                } else {
                    state.items.forEachIndexed { index, item ->
                        println("  ${index + 1}. ${describe(item)}")
                    }
                }
            }
        }
    }
}
