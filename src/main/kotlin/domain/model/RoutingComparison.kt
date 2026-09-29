package domain.model

data class RoutingComparison(
    val fewestHops: List<Warehouse>?,
    val optimalDistance: List<Warehouse>?,
    val bidirectional: List<Warehouse>?
)