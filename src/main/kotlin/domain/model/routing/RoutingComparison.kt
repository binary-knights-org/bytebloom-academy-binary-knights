package domain.model.routing

import domain.model.Warehouse

data class RoutingComparison(
    val fewestHops: List<Warehouse>?,
    val optimalDistance: List<Warehouse>?,
    val bidirectional: List<Warehouse>?
)
