package domain.model

data class SimulationResult(
    val packages: List<Package>,
    val before: Map<Package, Vehicle>,
    val after: Map<Package, Vehicle>,
    val breakdownEvent: BreakdownEvent
)
