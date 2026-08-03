package data.parser


import java.io.File

fun checkFileExists(file: File): Boolean {
    return file.exists()
}
