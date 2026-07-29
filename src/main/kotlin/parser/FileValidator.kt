package parser


import java.io.File

fun checkFileExists(file: File, parserName: String ): Boolean {
    if (!file.exists()) {
        println("WARNING ($parserName): File not found at path: ${file.path}")
        return true
    }
    return false
}
