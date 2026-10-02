package di

import domain.usecase.analytics.AnalyzeTreePerformanceUseCase
import domain.usecase.analytics.CalculateNetworkResilienceScoreUseCase
import domain.usecase.analytics.CalculatePricingUseCase

import domain.usecase.crud.packages.CreatePackageUseCase
import domain.usecase.crud.packages.DeletePackageUseCase
import domain.usecase.crud.packages.GetPackageByIdUseCase
import domain.usecase.crud.packages.UpdatePackageUseCase

import domain.usecase.crud.route.CreateRouteUseCase
import domain.usecase.crud.route.DeleteRouteUseCase
import domain.usecase.crud.route.GetRouteByIdUseCase
import domain.usecase.crud.route.UpdateRouteUseCase

import domain.usecase.crud.vehicle.CreateVehicleUseCase
import domain.usecase.crud.vehicle.DeleteVehicleUseCase
import domain.usecase.crud.vehicle.GetVehicleByIdUseCase
import domain.usecase.crud.vehicle.UpdateVehicleUseCase

import domain.usecase.crud.warehouse.CreateWarehouseUseCase
import domain.usecase.crud.warehouse.DeleteWarehouseUseCase
import domain.usecase.crud.warehouse.GetWarehouseByIdUseCase
import domain.usecase.crud.warehouse.UpdateWarehouseUseCase

import domain.usecase.shipment.AssignPackageToCargoQueueUseCase
import domain.usecase.shipment.CalculateAveragePackageWeightUseCase
import domain.usecase.shipment.FindPackagesByOriginUseCase
import domain.usecase.shipment.FindPackagesForConsolidationUseCase
import domain.usecase.shipment.ReroutePackageUseCase

import domain.usecase.vehicle.AddVehicleToHubUseCase
import domain.usecase.vehicle.AssignPackagesToVehicleUseCase
import domain.usecase.vehicle.DispatchVehicleUseCase
import domain.usecase.vehicle.FindStationedVehiclesByCapacityUseCase
import domain.usecase.vehicle.FindSuitableVehicleUseCase
import domain.usecase.vehicle.FindUnderutilizedVehiclesUseCase
import domain.usecase.vehicle.SuggestBestVehicleForPackageUseCase

import domain.usecase.warehouse.GetOverloadedWarehousesUseCase
import domain.usecase.warehouse.GetWarehouseLoadFactorUseCase
import domain.usecase.warehouse.TraceHubLineageUseCase

import domain.usecase.routing.CompareRoutingStrategiesUseCase
import domain.usecase.routing.FindBidirectionalRouteUseCase
import domain.usecase.routing.FindFewestHopsRouteUseCase
import domain.usecase.routing.FindOptimalPathUseCase

import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {

    // =========================
    // Package
    // =========================

    factory {
        CreatePackageUseCase(
            packageRepository = get(),
            validator = get()
        )
    }

    factory {
        DeletePackageUseCase(
            packageRepository = get()
        )
    }

    factory {
        GetPackageByIdUseCase(
            packageRepository = get()
        )
    }

    factory {
        UpdatePackageUseCase(
            packageRepository = get(),
            validator = get()
        )
    }

    // =========================
    // Route
    // =========================

    factory {
        CreateRouteUseCase(
            routeRepository = get(),
            validator = get()
        )
    }

    factory {
        DeleteRouteUseCase(
            routeRepository = get()
        )
    }

    factory {
        GetRouteByIdUseCase(
            routeRepository = get()
        )
    }

    factory {
        UpdateRouteUseCase(
            routeRepository = get(),
            validator = get()
        )
    }

    // =========================
    // Vehicle
    // =========================

    factory {
        CreateVehicleUseCase(
            vehicleRepository = get(),
            validator = get()
        )
    }

    factory {
        DeleteVehicleUseCase(
            vehicleRepository = get()
        )
    }

    factory {
        GetVehicleByIdUseCase(
            vehicleRepository = get()
        )
    }

    factory {
        UpdateVehicleUseCase(
            vehicleRepository = get(),
            validator = get()
        )
    }

    // =========================
    // Warehouse
    // =========================

    factory {
        CreateWarehouseUseCase(
            warehouseRepository = get(),
            validator = get()
        )
    }

    factory {
        DeleteWarehouseUseCase(
            warehouseRepository = get()
        )
    }

    factory {
        GetWarehouseByIdUseCase(
            warehouseRepository = get()
        )
    }

    factory {
        UpdateWarehouseUseCase(
            warehouseRepository = get(),
            validator = get()
        )
    }

    // =========================
    // Analytics
    // =========================

    factory {
        AnalyzeTreePerformanceUseCase()
    }

    factory {
        CalculateNetworkResilienceScoreUseCase()
    }

    factory {
        CalculatePricingUseCase(
            pricingEngine = get()
        )
    }

    // =========================
    // Shipment
    // =========================

    factory {
        AssignPackageToCargoQueueUseCase()
    }

    factory {
        CalculateAveragePackageWeightUseCase(
            warehouseRepository = get()
        )
    }

    factory {
        FindPackagesByOriginUseCase(
            packageRepository = get()
        )
    }

    factory {
        FindPackagesForConsolidationUseCase(
            packageRepository = get()
        )
    }

    factory {
        ReroutePackageUseCase(
            packageRepository = get(),
            warehouseRepository = get()
        )
    }

    // =========================
    // Vehicle Operations
    // =========================

    factory {
        AddVehicleToHubUseCase(
            vehicleRepository = get()
        )
    }

    factory {
        FindSuitableVehicleUseCase(
            vehicleRepository = get()
        )
    }

    factory {
        FindUnderutilizedVehiclesUseCase(
            vehicleRepository = get()
        )
    }

    factory {
        SuggestBestVehicleForPackageUseCase(
            vehicleRepository = get()
        )
    }

    factory {
        FindStationedVehiclesByCapacityUseCase(
            warehouseRepository = get()
        )
    }

    factory {
        DispatchVehicleUseCase(
            vehicleRepository = get(),
            warehouseRepository = get()
        )
    }

    factory {
        AssignPackagesToVehicleUseCase(
            findPackagesForConsolidationUseCase = get(),
            findSuitableVehicleUseCase = get()
        )
    }

    // =========================
    // Warehouse Operations
    // =========================

    factory {
        GetWarehouseLoadFactorUseCase(
            warehouseRepository = get()
        )
    }

    factory {
        GetOverloadedWarehousesUseCase(
            getWarehouseLoadFactorUseCase = get(),
            warehouseRepository = get()
        )
    }

    factory {
        TraceHubLineageUseCase()
    }

    // =========================
    // Routing
    // =========================

    factory {
        FindOptimalPathUseCase(
            router = get(named("optimalRouter"))
        )
    }

    factory {
        FindFewestHopsRouteUseCase(
            router = get(named("fewestHopsRouter"))
        )
    }

    factory {
        FindBidirectionalRouteUseCase(
            router = get(named("bidirectionalRouter"))
        )
    }

    factory {
        CompareRoutingStrategiesUseCase(
            findFewestHopsRouteUseCase = get(),
            findOptimalPathUseCase = get(),
            findBidirectionalRouteUseCase = get()
        )
    }
}
