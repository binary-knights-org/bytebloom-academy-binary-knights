package ui.cli

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jline.utils.NonBlockingReader

internal sealed interface Key {
    data class Character(val value: Char) : Key
    data object Backspace : Key
    data object Up : Key
    data object Down : Key
    data object Enter : Key
    data object Cancel : Key
    data object Ignored : Key
}

private const val END_OF_INPUT = -1
private const val CTRL_C = 3
private const val BACKSPACE_ALT = 8
private const val TAB = 9
private const val LINE_FEED = 10
private const val CARRIAGE_RETURN = 13
private const val ESCAPE = 27
private const val SPACE = 32
private const val DELETE = 127

private const val CSI = 91
private const val SS3 = 79
private const val ARROW_UP = 65
private const val ARROW_DOWN = 66
private const val PARAMETER_START = 0x30
private const val PARAMETER_END = 0x3F
private const val ESCAPE_TIMEOUT_MS = 25L

internal suspend fun NonBlockingReader.readKey(): Key =
    withContext(Dispatchers.IO) { decodeKey() }

private fun NonBlockingReader.decodeKey(): Key =
    when (val code = read()) {
        END_OF_INPUT, CTRL_C -> Key.Cancel
        CARRIAGE_RETURN, LINE_FEED -> Key.Enter
        BACKSPACE_ALT, DELETE -> Key.Backspace
        TAB -> Key.Down
        ESCAPE -> decodeEscapeSequence()
        else -> if (code >= SPACE && !Character.isISOControl(code)) {
            Key.Character(code.toChar())
        } else {
            Key.Ignored
        }
    }

private fun NonBlockingReader.decodeEscapeSequence(): Key {
    val introducer = read(ESCAPE_TIMEOUT_MS)

    return when {
        introducer < 0 -> Key.Cancel
        introducer == CSI || introducer == SS3 -> decodeControlSequence()
        else -> Key.Ignored
    }
}

private fun NonBlockingReader.decodeControlSequence(): Key {
    var code = read(ESCAPE_TIMEOUT_MS)
    while (code in PARAMETER_START..PARAMETER_END) {
        code = read(ESCAPE_TIMEOUT_MS)
    }

    return when (code) {
        ARROW_UP -> Key.Up
        ARROW_DOWN -> Key.Down
        else -> Key.Ignored
    }
}
