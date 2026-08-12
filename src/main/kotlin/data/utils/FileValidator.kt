package data.utils


import java.io.File

fun checkFileExists(file: File): Boolean {
    return file.exists()
}
