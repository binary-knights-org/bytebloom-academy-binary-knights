package domain.model

data class TreePerformanceAnalysis(
    val totalCount: Int,
    val unbalancedMaxSteps: Int,
    val unbalancedTotalSteps: Long,
    val unbalancedAvgSteps: Double,
    val balancedMaxSteps: Int,
    val balancedTotalSteps: Long,
    val balancedAvgSteps: Double
)
