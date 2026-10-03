package di

import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.algorithm.pathfinding.ShortestPathRouter
import org.koin.core.qualifier.named
import org.koin.dsl.module

val routingModule = module {

    single<ShortestPathRouter>(named("optimalRouter")) { OptimalTransitRouter(get()) }
    single<ShortestPathRouter>(named("fewestHopsRouter")) { LeastHopRouter(get()) }
    single<ShortestPathRouter>(named("bidirectionalRouter")) { BidirectionalBfsRouter(get()) }
}
