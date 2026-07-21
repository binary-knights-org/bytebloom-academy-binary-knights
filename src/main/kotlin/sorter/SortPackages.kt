package sorter

import dataholders.Package


private const val PRIORITY_URGENT_TEXT = "URGENT"
private const val PRIORITY_STANDARD_TEXT = "STANDARD"
private const val PRIORITY_LOW_TEXT = "LOW"

private const val RANK_URGENT = 3
private const val RANK_STANDARD = 2
private const val RANK_LOW = 1
private const val RANK_DEFAULT = 1


private fun getPriorityRank(priority: String): Int {
    return when (priority.uppercase()) {
        PRIORITY_URGENT_TEXT -> RANK_URGENT
        PRIORITY_STANDARD_TEXT -> RANK_STANDARD
        PRIORITY_LOW_TEXT -> RANK_LOW
        else -> RANK_DEFAULT
    }
}

private fun isBetterPackage(candidate: Package, currentBest: Package): Boolean {
    val candidateRank = getPriorityRank(candidate.priority)
    val currentBestRank = getPriorityRank(currentBest.priority)
    if (candidateRank > currentBestRank) {
        return true
    }

    val isSamePriority = candidateRank == currentBestRank
    val isHeavier = candidate.weight > currentBest.weight

    return isSamePriority && isHeavier
}

private fun findIndexOfBestPackage(packages: List<Package>, startIndex: Int): Int {
    var bestIndex = startIndex

    val nextElementIndex = startIndex + 1
    val totalPackages = packages.size
    for (currentIndex in nextElementIndex until totalPackages) {
        if (isBetterPackage(packages[currentIndex], packages[bestIndex])) {
            bestIndex = currentIndex
        }
    }

    return bestIndex
}

private fun swapPackages(packages: MutableList<Package>, firstIndex: Int, secondIndex: Int) {
    val tempPackage = packages[firstIndex]
    packages[firstIndex] = packages[secondIndex]
    packages[secondIndex] = tempPackage
}

fun sortPackagesByPriorityAndWeight(packages: List<Package>): List<Package> {
    val sortedPackages = packages.toMutableList()

    val firstElementIndex = 0
    val indexBeforeLast = sortedPackages.size - 1

    for (currentIndex in firstElementIndex until indexBeforeLast) {
        val bestPackageIndex = findIndexOfBestPackage(sortedPackages, currentIndex)
        if (bestPackageIndex != currentIndex) {
            swapPackages(sortedPackages, currentIndex, bestPackageIndex)
        }
    }

    return sortedPackages
}