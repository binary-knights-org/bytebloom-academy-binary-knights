package di

import domain.pricing.DispatchStrategy
import domain.pricing.EcoStrategy
import domain.pricing.ExpressStrategy
import domain.pricing.FragileStrategy
import domain.pricing.RoutePricingEngine
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val pricingModule = module {

    singleOf(::EcoStrategy) bind DispatchStrategy::class
    singleOf(::ExpressStrategy) { qualifier = named("expressStrategy") } bind DispatchStrategy::class
    singleOf(::FragileStrategy) { qualifier = named("fragileStrategy") } bind DispatchStrategy::class
    singleOf(::RoutePricingEngine)
}
