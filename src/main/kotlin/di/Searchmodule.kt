package di

import domain.usecase.search.SearchPackagesUseCase
import domain.usecase.search.SearchRoutesUseCase
import domain.usecase.search.SearchVehiclesUseCase
import domain.usecase.search.SearchWarehousesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val searchModule = module {
    factoryOf ( ::SearchRoutesUseCase )
    factoryOf ( ::SearchVehiclesUseCase)
    factoryOf (::SearchWarehousesUseCase )
    factoryOf(::SearchPackagesUseCase)
}
