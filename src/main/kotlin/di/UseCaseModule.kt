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
import domain.usecase.routing.CompareRoutingStrategiesUseCase
import domain.usecase.routing.FindBidirectionalRouteUseCase
import domain.usecase.routing.FindFewestHopsRouteUseCase
import domain.usecase.routing.FindOptimalPathUseCase
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
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {

    factoryOf(::CreatePackageUseCase)
    factoryOf(::DeletePackageUseCase)
    factoryOf(::GetPackageByIdUseCase)
    factoryOf(::UpdatePackageUseCase)

    factoryOf(::CreateRouteUseCase)
    factoryOf(::DeleteRouteUseCase)
    factoryOf(::GetRouteByIdUseCase)
    factoryOf(::UpdateRouteUseCase)

    factoryOf(::CreateVehicleUseCase)
    factoryOf(::DeleteVehicleUseCase)
    factoryOf(::GetVehicleByIdUseCase)
    factoryOf(::UpdateVehicleUseCase)

    factoryOf(::CreateWarehouseUseCase)
    factoryOf(::DeleteWarehouseUseCase)
    factoryOf(::GetWarehouseByIdUseCase)
    factoryOf(::UpdateWarehouseUseCase)

    factoryOf(::AnalyzeTreePerformanceUseCase)
    factoryOf(::CalculateNetworkResilienceScoreUseCase)
    factoryOf(::CalculatePricingUseCase)

    factoryOf(::AssignPackageToCargoQueueUseCase)
    factoryOf(::CalculateAveragePackageWeightUseCase)
    factoryOf(::FindPackagesByOriginUseCase)
    factoryOf(::FindPackagesForConsolidationUseCase)
    factoryOf(::ReroutePackageUseCase)

    factoryOf(::AddVehicleToHubUseCase)
    factoryOf(::FindSuitableVehicleUseCase)
    factoryOf(::FindUnderutilizedVehiclesUseCase)
    factoryOf(::SuggestBestVehicleForPackageUseCase)
    factoryOf(::FindStationedVehiclesByCapacityUseCase)
    factoryOf(::DispatchVehicleUseCase)
    factoryOf(::AssignPackagesToVehicleUseCase)

    factoryOf(::GetWarehouseLoadFactorUseCase)
    factoryOf(::GetOverloadedWarehousesUseCase)
    factoryOf(::TraceHubLineageUseCase)

    factoryOf(::FindOptimalPathUseCase)
    factoryOf(::FindFewestHopsRouteUseCase)
    factoryOf(::FindBidirectionalRouteUseCase)

    factoryOf(::CompareRoutingStrategiesUseCase)
}
