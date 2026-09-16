package data.local.csv

import java.io.File
import java.io.IOException
import java.io.FileNotFoundException

class CsvFileHandler(
    private val filePath: String,
    private val headerLinesToSkip: Int = 1
) {
    private val file = File(filePath)

    init {
        try {
            if (!file.exists()) {
                file.parentFile?.mkdirs()
                val created = file.createNewFile()
                if (!created) {
                    throw CsvFileCreationException(filePath)
                }
            }
        } catch (e: IOException) {
            throw CsvFileCreationException(filePath, e)
        } catch (e: SecurityException) {
            throw CsvFileCreationException(filePath, e)
        }
    }

    fun readLines(): List<String> {
        if (!file.exists()) throw CsvFileNotFoundException(filePath) // Throw 1

        return try {
            val lines = file.readLines()
            if (lines.size > headerLinesToSkip) {
                lines.drop(headerLinesToSkip)
            } else {
                emptyList()
            }
        } catch (e: IOException) {
            throw CsvReadException(e)
        }
    }

    fun appendLine(line: String) {
        validateBeforeAppend(line)

        try {
            file.appendText("$line\n")
        } catch (e: IOException) {
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

        try {
            val validLines = lines.filter { it.isNotBlank() }
            val content = validLines.joinToString(separator = "\n", postfix = "\n")
            file.appendText(content)
        } catch (e: IOException) {
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

        try {
            file.writeText("")
        } catch (e: IOException) {
            throw CsvWriteException(e)
        }
    }

    fun splitFields(line: String, delimiter: String = ","): List<String> {
        return line.split(delimiter).map { it.trim() }
    }
}
