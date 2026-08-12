package data.reader

import data.utils.checkFileExists
import java.io.File


private const val HEADER_LINES_TO_SKIP = 1


class CsvFileReader {
    fun readLines(filePath: String): List<String> {
        val file = File(filePath)
        if (!checkFileExists(file)) return emptyList()

        val lines = file.readLines().drop(HEADER_LINES_TO_SKIP)
        return lines
    }
}