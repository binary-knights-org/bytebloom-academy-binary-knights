package data.processing.reader

import data.utils.checkFileExists
import java.io.File

class CsvFileReader {
    fun readLines(filePath: String): List<String> {
        val file = File(filePath)
        if (!checkFileExists(file)) return emptyList()

        val lines = file.readLines().drop(HEADER_LINES_TO_SKIP)
        return lines
    }

    private companion object {
        const val HEADER_LINES_TO_SKIP = 1
    }
}
