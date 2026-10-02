package di

import com.google.common.truth.Truth.assertThat
import domain.pricing.DispatchStrategy
import domain.pricing.EcoStrategy
import domain.pricing.ExpressStrategy
import domain.pricing.FragileStrategy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.get

class PricingModuleTest : KoinTest {

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(pricingModule)
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should provide eco strategy`() {
        val strategy = get<DispatchStrategy>(named("ecoStrategy"))

        assertThat(strategy).isInstanceOf(EcoStrategy::class.java)
    }

    @Test
    fun `should provide express strategy`() {
        val strategy = get<DispatchStrategy>(named("expressStrategy"))

        assertThat(strategy).isInstanceOf(ExpressStrategy::class.java)
    }

    @Test
    fun `should provide fragile strategy`() {
        val strategy = get<DispatchStrategy>(named("fragileStrategy"))

        assertThat(strategy).isInstanceOf(FragileStrategy::class.java)
    }
}
