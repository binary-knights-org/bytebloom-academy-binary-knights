package di

import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.algorithm.pathfinding.ShortestPathRouter
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val routingModule = module {

    singleOf(::OptimalTransitRouter) bind ShortestPathRouter::class
    singleOf(::LeastHopRouter) { qualifier = named("fewestHopsRouter") } bind ShortestPathRouter::class
    singleOf(::BidirectionalBfsRouter) { qualifier = named("bidirectionalRouter") } bind ShortestPathRouter::class
}
