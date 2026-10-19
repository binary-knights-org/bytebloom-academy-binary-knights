package data.local.csv

import data.exception.CsvFileCreationException
import data.exception.CsvFileNotFoundException
import data.exception.CsvReadException
import java.io.File

class CsvFileHandler(
    private val filePath: String,
    private val headerLinesToSkip: Int = 1
) {
    private val file = File(filePath)

    init {
        createFileIfMissing()
    }

    fun readLines(): List<String> {
        if (!file.exists()) {
            throw CsvFileNotFoundException(filePath)
        }

        return runCatching {
            file.readLines().drop(headerLinesToSkip)
        }.getOrElse { error ->
            throw CsvReadException(error)
        }
    }

    fun splitFields(
        line: String,
        delimiter: String = DEFAULT_DELIMITER
    ): List<String> =
        line.split(delimiter).map(String::trim)

    private fun createFileIfMissing() {
        val isReady = runCatching {
            file.parentFile?.mkdirs()
            file.exists() || file.createNewFile()
        }.getOrElse { error ->
            throw CsvFileCreationException(filePath, error)
        }

        if (!isReady) {
            throw CsvFileCreationException(filePath)
        }
    }

    private companion object {
        const val DEFAULT_DELIMITER = ","
    }
}
