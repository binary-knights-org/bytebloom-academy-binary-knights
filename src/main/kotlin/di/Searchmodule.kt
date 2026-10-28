package di

import domain.usecase.search.SearchPackagesUseCase
import domain.usecase.search.SearchRoutesUseCase
import domain.usecase.search.SearchVehiclesUseCase
import domain.usecase.search.SearchWarehousesUseCase
import org.koin.dsl.module

val searchModule = module {
    factory { SearchPackagesUseCase(get()) }
    factory { SearchRoutesUseCase(get()) }
    factory { SearchVehiclesUseCase(get()) }
    factory { SearchWarehousesUseCase(get()) }
}
