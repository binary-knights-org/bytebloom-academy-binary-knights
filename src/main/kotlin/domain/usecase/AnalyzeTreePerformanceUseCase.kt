package domain.usecase

import domain.model.tree.BalancedTree
import domain.model.tree.UnbalancedBST
import domain.util.PackageDataGenerator


data class TreePerformanceAnalysis(
    val totalCount: Int,
    val unbalancedMaxSteps: Int,
    val unbalancedTotalSteps: Long,
    val unbalancedAvgSteps: Double,
    val balancedMaxSteps: Int,
    val balancedTotalSteps: Long,
    val balancedAvgSteps: Double
)

class AnalyzeTreePerformanceUseCase {
    operator fun invoke(count: Int = 1000): TreePerformanceAnalysis {
        val packageIds = PackageDataGenerator().generateSequentialIds()

        val unbalancedBst = packageIds.fold(UnbalancedBST()) { tree, id ->
            tree.insert(id)
        }

        val balancedBst = packageIds.fold(BalancedTree()) { tree, id ->
            tree.insert(id)
        }


        val unbalancedResults = packageIds.map { id -> unbalancedBst.searchWithStepCount(id) }
        val balancedResults = packageIds.map { id -> balancedBst.searchWithStepCount(id) }

        val unbalancedMaxSteps = unbalancedResults.map { it.second }.maxOrNull() ?: 0
        val unbalancedTotalSteps = unbalancedResults.sumOf { it.second.toLong() }
        val unbalancedAvg = if (count > 0) unbalancedTotalSteps.toDouble() / count else 0.0

        val balancedMaxSteps = balancedResults.map { it.second }.maxOrNull() ?: 0
        val balancedTotalSteps = balancedResults.sumOf { it.second.toLong() }
        val balancedAvg = if (count > 0) balancedTotalSteps.toDouble() / count else 0.0

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
}
