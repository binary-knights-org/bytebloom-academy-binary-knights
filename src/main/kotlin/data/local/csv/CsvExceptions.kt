package data.local.csv

import java.io.IOException

class CsvFileNotFoundException(
    filePath: String,
    cause: Throwable? = null
) : IOException("CSV file not found at path: $filePath", cause)


class CsvFileCreationException(
    filePath: String,
    cause: Throwable? = null
) : IOException("Failed to create CSV file at path: $filePath", cause)


class CsvEmptyLineException :
    IllegalArgumentException("Cannot append an empty or blank line to the CSV file.")


class CsvReadException(
    cause: Throwable? = null
) : IOException("An error occurred while reading the CSV file.", cause)


class CsvWriteException(
    cause: Throwable? = null
) : IOException("An error occurred while writing to the CSV file.", cause)
