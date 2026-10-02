package di

import domain.pricing.DispatchStrategy
import domain.pricing.EcoStrategy
import domain.pricing.ExpressStrategy
import domain.pricing.FragileStrategy
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
}
