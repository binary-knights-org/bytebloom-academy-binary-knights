
package data.local.csv

import data.exception.CsvFileNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseCsvDataSource<T>(
    private val csvHandler: CsvFileHandler
) {
    protected suspend fun readAll(): List<T> =
        withContext(Dispatchers.IO) {
            readLinesOrEmpty()
                .mapNotNull(::parseLine)
        }

    protected abstract fun parseLine(line: String): T?

    protected fun splitFields(line: String): List<String> =
        csvHandler.splitFields(line)

    private fun readLinesOrEmpty(): List<String> =
        runCatching { csvHandler.readLines() }
            .getOrElse { error ->
                if (error is CsvFileNotFoundException) emptyList()
                else throw error
            }

    protected fun normalizePrefix(
        value: String,
        prefix: String
    ): String =
        if (value.startsWith(prefix)) value else "$prefix$value"
}
