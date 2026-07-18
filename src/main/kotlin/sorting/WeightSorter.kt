package sorting
import models.Package

fun sortPackagesByWeight(packages: List<Package>): List<Package> {
    val sortedPackages = packages.toMutableList()
    val packageCount = sortedPackages.size

    for (i in 0 until packageCount - 1) {
        var heaviestPackageIndex = i

        for (j in i + 1 until packageCount) {
            if (sortedPackages[j].weight > sortedPackages[heaviestPackageIndex].weight) {
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