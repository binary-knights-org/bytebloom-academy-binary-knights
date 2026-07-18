package sorting

data class Package(
    val packageId: String,
    val weightKg: Double,
    val priority: String,
    val destinationHubId: String
)

fun sortPackagesByWeight(packages: List<Package>): List<Package> {
    val sortedPackages = packages.toMutableList()
    val packageCount = sortedPackages.size

    for (i in 0 until packageCount - 1) {
        var heaviestPackageIndex = i

        for (j in i + 1 until packageCount) {
            if (sortedPackages[j].weightKg > sortedPackages[heaviestPackageIndex].weightKg) {
                heaviestPackageIndex = j
            }
        }

        if (heaviestPackageIndex != i) {
            val packageToSwap = sortedPackages[heaviestPackageIndex]
            sortedPackages[heaviestPackageIndex] = sortedPackages[i]
            sortedPackages[i] = packageToSwap
        }
    }

    return sortedPackages
}