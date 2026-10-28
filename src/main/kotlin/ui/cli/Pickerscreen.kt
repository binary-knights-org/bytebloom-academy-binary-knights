package ui.cli

import org.jline.terminal.Terminal
import ui.cli.Ansi.BLUE
import ui.cli.Ansi.CLEAR_SCREEN
import ui.cli.Ansi.CYAN
import ui.cli.Ansi.GRAY
import ui.cli.Ansi.RED
import ui.cli.Ansi.RESET
import ui.cli.Ansi.REVERSE
import ui.cli.Ansi.YELLOW

private const val PROMPT = "Search> "
private const val HINT = "(type to search | Up/Down or Tab to move | Enter to select | Esc to cancel)"
private const val PROMPT_ROW = 2
private const val VISIBLE_RESULTS = 10

internal fun <T> Terminal.render(title: String, ui: PickerUi<T>, describe: (T) -> String) {
    val prompt = "$PROMPT${ui.text}"
    val spinner = if (ui.loading) "  ${GRAY}searching...$RESET" else ""

    writer().apply {
        print("$CLEAR_SCREEN$BLUE$title$RESET  $GRAY$HINT$RESET\r\n")
        print("$prompt$spinner\r\n")
        print(resultsBlock(ui, describe))
        print("\u001B[$PROMPT_ROW;${prompt.length + 1}H")
        flush()
    }
}

internal fun Terminal.restore(attributes: org.jline.terminal.Attributes) {
    setAttributes(attributes)
    writer().print("$RESET$CLEAR_SCREEN")
    writer().flush()
}

private fun <T> resultsBlock(ui: PickerUi<T>, describe: (T) -> String): String {
    val items = ui.items
    return when {
        ui.error != null -> "$RED${ui.error}$RESET\r\n"
        items == null -> ""
        items.isEmpty() -> "${YELLOW}No results found.$RESET\r\n"
        else -> itemsBlock(items, ui.selected, describe)
    }
}

private fun <T> itemsBlock(items: List<T>, selected: Int, describe: (T) -> String): String {
    val start = (selected - VISIBLE_RESULTS / 2).coerceIn(0, maxOf(0, items.size - VISIBLE_RESULTS))
    val end = minOf(items.size, start + VISIBLE_RESULTS)

    return buildString {
        append("$CYAN${items.size} result(s)$RESET\r\n")
        for (index in start until end) {
            val line = describe(items[index])
            append(if (index == selected) "$REVERSE> $line$RESET" else "  $line")
            append("\r\n")
        }
        if (end < items.size) {
            append("$GRAY  ... ${items.size - end} more below$RESET\r\n")
        }
    }
}
