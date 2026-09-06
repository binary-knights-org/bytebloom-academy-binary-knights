package data.reader

import java.io.File

class CsvFileWriter {

    fun appendLine(filePath: String, line: String): Boolean {
        return try {
            val file = File(filePath).appendText("$line\n")
            true
        } catch (exception: Exception) {
            false
        }
    }
}