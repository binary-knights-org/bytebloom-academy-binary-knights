package sorter
import dataholders.Package

fun priorityValue(priority: String): Int {
    return when (priority.uppercase()) {
        "URGENT" -> 3
        "STANDARD" -> 2
        "LOW" -> 1
        else -> 1
    }
}

fun sortPackagesByPriorityAndWeight(packages: List<Package>): List<Package> {
    val sortedPackages = packages.toMutableList()
    val packageCount = sortedPackages.size

    for (i in 0 until packageCount - 1) {
        var bestIndex = i
        var currentHighestPriority = priorityValue(sortedPackages[bestIndex].priority)
        var currentHighestWeight = sortedPackages[bestIndex].weight

        for (j in i + 1 until packageCount) {
            val scanPriority = priorityValue(sortedPackages[j].priority)
            val scanWeight = sortedPackages[j].weight

            if (scanPriority > currentHighestPriority) {
                bestIndex = j
                currentHighestPriority = scanPriority
                currentHighestWeight = scanWeight

            } else if (scanPriority == currentHighestPriority) {
                if (scanWeight > currentHighestWeight) {
                    bestIndex = j
                    currentHighestPriority = scanPriority
                    currentHighestWeight = scanWeight
                }
            }
        }
        if (bestIndex != i) {
            val packageToSwap = sortedPackages[bestIndex]
            sortedPackages[bestIndex] = sortedPackages[i]
            sortedPackages[i] = packageToSwap
        }
    }
    
    return sortedPackages
}