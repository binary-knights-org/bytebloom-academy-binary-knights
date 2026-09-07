package domain.util

class PackageDataGenerator {

    fun generateSequentialIds(count: Int = DEFAULT_PACKAGE_COUNT): List<String> {
        return List(count) { index ->
            "PKG-" + (index + 1).toString().padStart(PACKAGE_ID_LENGTH, '0')
        }
    }

    private companion object {
        const val DEFAULT_PACKAGE_COUNT = 1000
        const val PACKAGE_ID_LENGTH = 6
    }
}
