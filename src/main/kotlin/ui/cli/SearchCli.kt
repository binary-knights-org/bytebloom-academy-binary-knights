package ui.cli

import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse
import domain.service.SearchState
import domain.usecase.search.SearchPackagesUseCase
import domain.usecase.search.SearchRoutesUseCase
import domain.usecase.search.SearchVehiclesUseCase
import domain.usecase.search.SearchWarehousesUseCase
import kotlinx.coroutines.flow.Flow

internal class SearchCli(
    private val picker: LivePicker,
    private val searchPackages: SearchPackagesUseCase,
    private val searchRoutes: SearchRoutesUseCase,
    private val searchVehicles: SearchVehiclesUseCase,
    private val searchWarehouses: SearchWarehousesUseCase
) {
    suspend fun run() {
        println("[SEARCH]")
        println("1. Packages\n2. Routes\n3. Vehicles\n4. Warehouses")
        print("Choose what to search: ")

        when (readInputLine()) {
            "1" -> browse("Search packages", searchPackages::invoke, Package::describe)
            "2" -> browse("Search routes", searchRoutes::invoke, Route::describe)
            "3" -> browse("Search vehicles", searchVehicles::invoke, Vehicle::describe)
            "4" -> browse("Search warehouses", searchWarehouses::invoke, Warehouse::describe)
            else -> println("Invalid option.")
        }
    }

    private suspend fun <T> browse(
        title: String,
        search: (Flow<String>) -> Flow<SearchState<T>>,
        describe: (T) -> String
    ) {
        val item = picker.pick(title, search, describe)
        println(if (item == null) "Search cancelled." else "Selected: ${describe(item)}")
    }
}
