package ui.cli

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun readInputLine(): String? =
    withContext(Dispatchers.IO) { readlnOrNull()?.trim() }
