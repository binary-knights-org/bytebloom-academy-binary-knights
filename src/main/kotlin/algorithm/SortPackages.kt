package algorithm

import utils.getPriorityRank
import dataholder.PackageRaw

private fun swapPackages(packages: MutableList<PackageRaw>, firstIndex: Int, secondIndex: Int) {
    val tempPackage = packages[firstIndex]
    packages[firstIndex] = packages[secondIndex]
    packages[secondIndex] = tempPackage
}

private fun isHeavier(packageToCheck: PackageRaw, referencePackage: PackageRaw): Boolean {
    return packageToCheck.weight > referencePackage.weight
}

private fun hasPrecedence(packageToCheck: PackageRaw, referencePackage: PackageRaw): Boolean {
    val rankToCheck = getPriorityRank(packageToCheck.priority)
    val referenceRank = getPriorityRank(referencePackage.priority)

    if (rankToCheck != referenceRank) {
        return rankToCheck > referenceRank
    }
    return isHeavier(packageToCheck, referencePackage)
}

private fun findHighestPriorityIndex(packages: List<PackageRaw>, startIndex: Int): Int {
    var highestPriorityIndex = startIndex
    val nextElementIndex = startIndex + 1
    val totalPackages = packages.size

    for (currentIndex in nextElementIndex until totalPackages) {
        if (hasPrecedence(packages[currentIndex], packages[highestPriorityIndex])) {
            highestPriorityIndex = currentIndex
        }
    }

    return highestPriorityIndex
}

fun sortPackagesByImportance(packages: List<PackageRaw>): List<PackageRaw> {
    val sortedPackages = packages.toMutableList()
    val firstElementIndex = 0
    val indexBeforeLast = sortedPackages.size - 1

    for (currentIndex in firstElementIndex until indexBeforeLast) {
        val nextIndex = findHighestPriorityIndex(sortedPackages, currentIndex)
        if (nextIndex != currentIndex) {
            swapPackages(sortedPackages, currentIndex, nextIndex)
        }
    }

    return sortedPackages
}