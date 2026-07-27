package utils


import java.io.File

fun isMissingFile(file: File, parserName: String ): Boolean {
    if (!file.exists()) {
        println("WARNING ($parserName): File not found at path: ${file.path}")
        return true
    }
    return false
}