package domain.algorithm.sorting

import domain.model.Package
import domain.model.Priority

class ImportanceSelectionSorter {

    fun sortPackagesByImportance(packages: List<Package>): List<Package> {
        val sortedPackages = packages.toMutableList()

        for (currentIndex in START_INDEX until sortedPackages.lastIndex) {
            val highestPriorityIndex = findHighestPriorityIndex(sortedPackages, currentIndex)
            if (highestPriorityIndex != currentIndex) {
                swapPackages(sortedPackages, currentIndex, highestPriorityIndex)
            }
        }
        return sortedPackages
    }

    private fun findHighestPriorityIndex(packages: List<Package>, startIndex: Int): Int {
        var highestPriorityIndex = startIndex

        for (currentIndex in startIndex + NEXT_INDEX_OFFSET until packages.size) {
            if (hasHigherPriority(packages[currentIndex], packages[highestPriorityIndex])) {
                highestPriorityIndex = currentIndex
            }
        }
        return highestPriorityIndex
    }

    private fun hasHigherPriority(packageToCheck: Package, referencePackage: Package): Boolean {
        val rankToCheck = getPriorityRank(packageToCheck.priority)
        val referenceRank = getPriorityRank(referencePackage.priority)

        return if (rankToCheck == referenceRank) {
            packageToCheck.weight > referencePackage.weight
        } else {
            rankToCheck > referenceRank
        }
    }

    private fun getPriorityRank(priority: Priority): Int = when (priority) {
        Priority.URGENT -> RANK_URGENT
        Priority.STANDARD -> RANK_STANDARD
        Priority.LOW -> RANK_LOW
    }

    private fun swapPackages(packages: MutableList<Package>, firstIndex: Int, secondIndex: Int) {
        val tempPackage = packages[firstIndex]
        packages[firstIndex] = packages[secondIndex]
        packages[secondIndex] = tempPackage
    }

    private companion object {
        const val START_INDEX = 0
        const val NEXT_INDEX_OFFSET = 1

        const val RANK_URGENT = 3
        const val RANK_STANDARD = 2
        const val RANK_LOW = 1
    }
}
