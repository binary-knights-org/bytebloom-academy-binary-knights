package algorithm

import dataholder.PackageRaw

private fun swapPackages(packages: MutableList<PackageRaw>, firstIndex: Int, secondIndex: Int) {
    val tempPackage = packages[firstIndex]
    packages[firstIndex] = packages[secondIndex]
    packages[secondIndex] = tempPackage
}

private fun isHeavier(packageToCheck: PackageRaw, referencePackage: PackageRaw): Boolean {
    return packageToCheck.weight > referencePackage.weight
}

private fun hasHigherPriority(packageToCheck: PackageRaw, referencePackage: PackageRaw): Boolean {
    val rankToCheck = getPriorityRank(packageToCheck.priority)
    val referenceRank = getPriorityRank(referencePackage.priority)
    return if (rankToCheck == referenceRank) isHeavier(
        packageToCheck,
        referencePackage
    ) else rankToCheck > referenceRank
}

private fun findHighestPriorityIndex(packages: List<PackageRaw>, startIndex: Int): Int {
    var highestPriorityIndex = startIndex

    for (currentIndex in startIndex + 1 until packages.size) {
        if (hasHigherPriority(packages[currentIndex], packages[highestPriorityIndex]))
            highestPriorityIndex = currentIndex
    }
    return highestPriorityIndex
}

fun sortPackagesByImportance(packages: List<PackageRaw>): List<PackageRaw> {
    val sortedPackages = packages.toMutableList()

    for (currentIndex in 0 until sortedPackages.lastIndex) {
        val nextIndex = findHighestPriorityIndex(sortedPackages, currentIndex)
        if (nextIndex != currentIndex) swapPackages(sortedPackages, currentIndex, nextIndex)
    }
    return sortedPackages
}