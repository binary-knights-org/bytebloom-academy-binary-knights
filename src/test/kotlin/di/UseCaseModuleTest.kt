package di

import com.google.common.truth.Truth.assertThat

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

import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

class UseCaseModuleTest : KoinTest {

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single { mockk<domain.repository.PackageRepository>() }
                    single { mockk<domain.repository.RouteRepository>() }
                    single { mockk<domain.repository.VehicleRepository>() }
                    single { mockk<domain.repository.WarehouseRepository>() }
                },
                validatorModule,
                pricingModule,
                routingModule,
                useCaseModule
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    // Analytics

    @Test
    fun `should provide analyze tree performance use case`() {
        val useCase = get<AnalyzeTreePerformanceUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide calculate network resilience score use case`() {
        val useCase = get<CalculateNetworkResilienceScoreUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide calculate pricing use case`() {
        val useCase = get<CalculatePricingUseCase>()
        assertThat(useCase).isNotNull()
    }

    // Package

    @Test
    fun `should provide create package use case`() {
        val useCase = get<CreatePackageUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide delete package use case`() {
        val useCase = get<DeletePackageUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide get package by id use case`() {
        val useCase = get<GetPackageByIdUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide update package use case`() {
        val useCase = get<UpdatePackageUseCase>()
        assertThat(useCase).isNotNull()
    }

    // Route

    @Test
    fun `should provide create route use case`() {
        val useCase = get<CreateRouteUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide delete route use case`() {
        val useCase = get<DeleteRouteUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide get route by id use case`() {
        val useCase = get<GetRouteByIdUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide update route use case`() {
        val useCase = get<UpdateRouteUseCase>()
        assertThat(useCase).isNotNull()
    }

    // Vehicle

    @Test
    fun `should provide create vehicle use case`() {
        val useCase = get<CreateVehicleUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide delete vehicle use case`() {
        val useCase = get<DeleteVehicleUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide get vehicle by id use case`() {
        val useCase = get<GetVehicleByIdUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide update vehicle use case`() {
        val useCase = get<UpdateVehicleUseCase>()
        assertThat(useCase).isNotNull()
    }

    // Warehouse

    @Test
    fun `should provide create warehouse use case`() {
        val useCase = get<CreateWarehouseUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide delete warehouse use case`() {
        val useCase = get<DeleteWarehouseUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide get warehouse by id use case`() {
        val useCase = get<GetWarehouseByIdUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide update warehouse use case`() {
        val useCase = get<UpdateWarehouseUseCase>()
        assertThat(useCase).isNotNull()
    }

    // Shipment

    @Test
    fun `should provide assign package to cargo queue use case`() {
        val useCase = get<AssignPackageToCargoQueueUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide calculate average package weight use case`() {
        val useCase = get<CalculateAveragePackageWeightUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide find packages by origin use case`() {
        val useCase = get<FindPackagesByOriginUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide find packages for consolidation use case`() {
        val useCase = get<FindPackagesForConsolidationUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide reroute package use case`() {
        val useCase = get<ReroutePackageUseCase>()
        assertThat(useCase).isNotNull()
    }

    // Vehicle Operations

    @Test
    fun `should provide add vehicle to hub use case`() {
        val useCase = get<AddVehicleToHubUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide assign packages to vehicle use case`() {
        val useCase = get<AssignPackagesToVehicleUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide dispatch vehicle use case`() {
        val useCase = get<DispatchVehicleUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide find stationed vehicles by capacity use case`() {
        val useCase = get<FindStationedVehiclesByCapacityUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide find suitable vehicle use case`() {
        val useCase = get<FindSuitableVehicleUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide find underutilized vehicles use case`() {
        val useCase = get<FindUnderutilizedVehiclesUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide suggest best vehicle for package use case`() {
        val useCase = get<SuggestBestVehicleForPackageUseCase>()
        assertThat(useCase).isNotNull()
    }

    // Warehouse Operations

    @Test
    fun `should provide get warehouse load factor use case`() {
        val useCase = get<GetWarehouseLoadFactorUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide get overloaded warehouses use case`() {
        val useCase = get<GetOverloadedWarehousesUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide trace hub lineage use case`() {
        val useCase = get<TraceHubLineageUseCase>()
        assertThat(useCase).isNotNull()
    }

    // Routing

    @Test
    fun `should provide optimal path use case`() {
        val useCase = get<FindOptimalPathUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide fewest hops route use case`() {
        val useCase = get<FindFewestHopsRouteUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide bidirectional route use case`() {
        val useCase = get<FindBidirectionalRouteUseCase>()
        assertThat(useCase).isNotNull()
    }

    @Test
    fun `should provide compare routing strategies use case`() {
        val useCase = get<CompareRoutingStrategiesUseCase>()
        assertThat(useCase).isNotNull()
    }
}
