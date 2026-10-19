package domain.usecase.analytics

import domain.algorithm.tree.BalancedTree
import domain.algorithm.tree.UnbalancedBST
import domain.model.Package
import domain.model.routing.TreePerformanceAnalysis

class AnalyzeTreePerformanceUseCase {
    operator fun invoke(count: Int = DEFAULT_PACKAGE_COUNT): TreePerformanceAnalysis {
        val packageIds = generateSequentialIds(count)

        val unbalancedBst = packageIds.fold(UnbalancedBST()) { tree, id -> tree.insert(id) }
        val balancedBst = packageIds.fold(BalancedTree()) { tree, id -> tree.insert(id) }

        val unbalancedResults = packageIds.map { id -> unbalancedBst.searchWithStepCount(id) }
        val balancedResults = packageIds.map { id -> balancedBst.searchWithStepCount(id) }

        val unbalancedMaxSteps = unbalancedResults.maxOfOrNull { it.second } ?: 0
        val unbalancedTotalSteps = unbalancedResults.sumOf { it.second.toLong() }
        val unbalancedAvg = if (count > 0) unbalancedTotalSteps.toDouble() / count else ZERO_AVG_STEPS

        val balancedMaxSteps = balancedResults.maxOfOrNull { it.second } ?: 0
        val balancedTotalSteps = balancedResults.sumOf { it.second.toLong() }
        val balancedAvg = if (count > 0) balancedTotalSteps.toDouble() / count else ZERO_AVG_STEPS

        return TreePerformanceAnalysis(
            totalCount = count,
            unbalancedMaxSteps = unbalancedMaxSteps,
            unbalancedTotalSteps = unbalancedTotalSteps,
            unbalancedAvgSteps = unbalancedAvg,
            balancedMaxSteps = balancedMaxSteps,
            balancedTotalSteps = balancedTotalSteps,
            balancedAvgSteps = balancedAvg
        )
    }

    private fun generateSequentialIds(count: Int = DEFAULT_PACKAGE_COUNT): List<String> {
        return List(count) { index ->
            val formattedIndex = (index + 1).toString().padStart(PACKAGE_ID_PADDING_LENGTH, '0')
            "${Package.PACKAGE_ID_PREFIX}$formattedIndex"
        }
    }

    private companion object {
        private const val DEFAULT_PACKAGE_COUNT = 1000
        private const val PACKAGE_ID_PADDING_LENGTH = 6
        private const val ZERO_AVG_STEPS = 0.0
    }
}
