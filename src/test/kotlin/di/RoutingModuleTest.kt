package di

import com.google.common.truth.Truth.assertThat
import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.algorithm.pathfinding.ShortestPathRouter
import domain.repository.WarehouseRepository
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

class RoutingModuleTest : KoinTest {

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single<WarehouseRepository> { mockk() }
                },
                routingModule
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should provide optimal router`() {
        val router = get<ShortestPathRouter>(named("optimalRouter"))
        assertThat(router).isInstanceOf(OptimalTransitRouter::class.java)
    }

    @Test
    fun `should provide fewest hops router`() {
        val router = get<ShortestPathRouter>(named("fewestHopsRouter"))
        assertThat(router).isInstanceOf(LeastHopRouter::class.java)
    }

    @Test
    fun `should provide bidirectional router`() {
        val router = get<ShortestPathRouter>(named("bidirectionalRouter"))
        assertThat(router).isInstanceOf(BidirectionalBfsRouter::class.java)
    }
}
