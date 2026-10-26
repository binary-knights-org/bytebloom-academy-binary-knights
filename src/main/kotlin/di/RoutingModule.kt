package di

import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.CachingShortestPathRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.algorithm.pathfinding.LruRouteCache
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.algorithm.pathfinding.ShortestPathRouter
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val routingModule = module {

    singleOf(::LeastHopRouter) { qualifier = named("fewestHopsRouter") } bind ShortestPathRouter::class
    singleOf(::BidirectionalBfsRouter) { qualifier = named("bidirectionalRouter") } bind ShortestPathRouter::class
    singleOf(::OptimalTransitRouter)
    single { LruRouteCache() }
    single<ShortestPathRouter> { CachingShortestPathRouter(
        dijkstraRouter = get<OptimalTransitRouter>(),
            cache = get<LruRouteCache>()
        )
    }
}
