package data.local.csv

import data.exception.CsvEmptyLineException
import data.exception.CsvFileCreationException
import data.exception.CsvFileNotFoundException
import data.exception.CsvReadException
import data.exception.CsvWriteException
import java.io.File

class CsvFileHandler(
    private val filePath: String,
    private val headerLinesToSkip: Int = 1
) {
    private val file = File(filePath)

    init {
        runCatching {
            if (!file.exists()) {
                file.parentFile?.mkdirs()
                val created = file.createNewFile()
                if (!created) {
                    throw CsvFileCreationException(filePath)
                }
            }
        }.getOrElse { e ->
            throw CsvFileCreationException(filePath, e)
        }
    }

    fun readLines(): List<String> {
        if (!file.exists()) throw CsvFileNotFoundException(filePath)

        return runCatching {
            val lines = file.readLines()
            if (lines.size > headerLinesToSkip) {
                lines.drop(headerLinesToSkip)
            } else {
                emptyList()
            }
        }.getOrElse { e ->
            throw CsvReadException(e)
        }
    }

    fun appendLine(line: String) {
        validateBeforeAppend(line)

        runCatching {
            file.appendText("$line\n")
        }.getOrElse { e ->
            throw CsvWriteException(e)
        }
    }

    private fun validateBeforeAppend(line: String) {
        if (line.isBlank()) throw CsvEmptyLineException()
        if (!file.exists()) throw CsvFileNotFoundException(filePath)
    }

    fun appendLines(lines: List<String>) {
        if (lines.isEmpty()) return

        validateBeforeAppend(lines)

        runCatching {
            val validLines = lines.filter { it.isNotBlank() }
            val content = validLines.joinToString(separator = "\n", postfix = "\n")
            file.appendText(content)
        }.getOrElse { e ->
            throw CsvWriteException(e)
        }
    }

    private fun validateBeforeAppend(lines: List<String>) {
        if (!file.exists()) throw CsvFileNotFoundException(filePath)

        val validLines = lines.filter { it.isNotBlank() }
        if (validLines.isEmpty()) throw CsvEmptyLineException()
    }

    fun clearFile() {
        if (!file.exists()) throw CsvFileNotFoundException(filePath)

        runCatching {
            file.writeText("")
        }.getOrElse { e ->
            throw CsvWriteException(e)
        }
    }

    fun splitFields(line: String, delimiter: String = ","): List<String> {
        return line.split(delimiter).map { it.trim() }
    }

    fun rewriteLines(lines: List<String>) {
        if (!file.exists()) {
            throw CsvFileNotFoundException(filePath)
        }

        runCatching {
            val header = file.readLines().take(headerLinesToSkip)
            val validLines = lines.filter { it.isNotBlank() }

            val content = (header + validLines).joinToString(
                separator = "\n",
                postfix = "\n"
            )

            file.writeText(content)
        }.getOrElse { e ->
            throw CsvWriteException(e)
        }
    }
}
