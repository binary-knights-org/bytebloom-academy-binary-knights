package algorithm

import utils.getPriorityRank
import dataholder.PackageRaw


private fun swapPackages(packages: MutableList<PackageRaw>, firstIndex: Int, secondIndex: Int) {
    val tempPackage = packages[firstIndex]
    packages[firstIndex] = packages[secondIndex]
    packages[secondIndex] = tempPackage
}


// -------------- Priority --------------

private fun hasHigherPriority(packageToCheck: PackageRaw, referencePackage: PackageRaw): Boolean {
    val rankToCheck = getPriorityRank(packageToCheck.priority)
    val referenceRank = getPriorityRank(referencePackage.priority)
    return rankToCheck > referenceRank
}

private fun findHighestPriorityIndex(packages: List<PackageRaw>, startIndex: Int): Int {
    var highestPriorityIndex = startIndex

    val nextElementIndex = startIndex + 1
    val totalPackages = packages.size

    for (currentIndex in nextElementIndex until totalPackages) {
        if (hasHigherPriority(packages[currentIndex], packages[highestPriorityIndex])) {
            highestPriorityIndex = currentIndex
        }
    }

    return highestPriorityIndex
}


fun sortPackagesByPriority(packages: List<PackageRaw>): List<PackageRaw> {
    val sortedPackages = packages.toMutableList()

    val firstElementIndex = 0
    val indexBeforeLast = sortedPackages.size - 1

    for (currentIndex in firstElementIndex until indexBeforeLast) {
        val highestPriorityIndex = findHighestPriorityIndex(sortedPackages, currentIndex)
        if (highestPriorityIndex != currentIndex) {
            swapPackages(sortedPackages, currentIndex, highestPriorityIndex)
        }
    }

    return sortedPackages
}


// -------------- Weight --------------

private fun isHeavier(packageToCheck: PackageRaw, referencePackage: PackageRaw): Boolean {
    return packageToCheck.weight > referencePackage.weight
}

private fun findHeaviestPackageIndex(packages: List<PackageRaw>, startIndex: Int): Int {
    var heaviestPackageIndex = startIndex

    val nextElementIndex = startIndex + 1
    val totalPackages = packages.size

    for (currentIndex in nextElementIndex until totalPackages) {
        if (isHeavier(packages[currentIndex], packages[heaviestPackageIndex])) {
            heaviestPackageIndex = currentIndex
        }
    }

    return heaviestPackageIndex
}

fun sortPackagesByWeight(packages: List<PackageRaw>): List<PackageRaw> {
    val sortedPackages = packages.toMutableList()

    val firstElementIndex = 0
    val indexBeforeLast = sortedPackages.size - 1

    for (currentIndex in firstElementIndex until indexBeforeLast) {
        val heaviestPackageIndex = findHighestPriorityIndex(sortedPackages, currentIndex)
        if (heaviestPackageIndex != currentIndex) {
            swapPackages(sortedPackages, currentIndex, heaviestPackageIndex)
        }
    }

    return sortedPackages
}