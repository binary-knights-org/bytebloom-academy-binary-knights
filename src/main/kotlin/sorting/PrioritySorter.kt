
package sorting
import models.Package

fun priorityValue(priority: String): Int {
    return when (priority.uppercase()) {
        "URGENT" -> 3
        "STANDARD" -> 2
        "LOW" -> 1
        else -> 1
    }
}


fun sortPackagesByPriority(packages: List<Package>): List<Package> {

    val sortedPackages = packages.toMutableList()
    val packageCount = sortedPackages.size

    for (i in 0 until packageCount - 1) {

        var highestPriorityIndex = i

        for (j in i + 1 until packageCount) {

            if (priorityValue(sortedPackages[j].priority) >
                priorityValue(sortedPackages[highestPriorityIndex].priority)) {

                highestPriorityIndex = j
            }
        }


        if (highestPriorityIndex != i) {

            val packageToSwap = sortedPackages[highestPriorityIndex]

            sortedPackages[highestPriorityIndex] = sortedPackages[i]

            sortedPackages[i] = packageToSwap
        }
    }

    return sortedPackages
}