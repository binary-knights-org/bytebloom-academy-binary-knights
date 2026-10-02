package di

import domain.pricing.DispatchStrategy
import domain.pricing.EcoStrategy
import domain.pricing.ExpressStrategy
import domain.pricing.FragileStrategy
import domain.pricing.RoutePricingEngine
import org.koin.core.qualifier.named
import org.koin.dsl.module

val pricingModule = module {

    single<DispatchStrategy>(named("ecoStrategy")) {
        EcoStrategy()
    }

    single<DispatchStrategy>(named("expressStrategy")) {
        ExpressStrategy()
    }

    single<DispatchStrategy>(named("fragileStrategy")) {
        FragileStrategy()
    }

    single {
        RoutePricingEngine(
            strategy = get(named("ecoStrategy"))
        )
    }
}
