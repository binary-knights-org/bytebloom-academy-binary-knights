package di

import com.google.common.truth.Truth.assertThat
import domain.pricing.RoutePricingEngine
import domain.repository.PackageRepository
import domain.repository.RouteRepository
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
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
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.dsl.module

class UseCaseModuleTest : KoinTest {

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single<PackageRepository> { mockk() }
                    single<RouteRepository> { mockk() }
                    single<VehicleRepository> { mockk() }
                    single<WarehouseRepository> { mockk() }
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

    @Test
    fun `should resolve all use cases from Koin`() {
        assertThat(get<AnalyzeTreePerformanceUseCase>()).isNotNull()
        assertThat(get<CalculateNetworkResilienceScoreUseCase>()).isNotNull()
        assertThat(get<CalculatePricingUseCase>()).isNotNull()

        assertThat(get<CreatePackageUseCase>()).isNotNull()
        assertThat(get<DeletePackageUseCase>()).isNotNull()
        assertThat(get<GetPackageByIdUseCase>()).isNotNull()
        assertThat(get<UpdatePackageUseCase>()).isNotNull()

        assertThat(get<CreateRouteUseCase>()).isNotNull()
        assertThat(get<DeleteRouteUseCase>()).isNotNull()
        assertThat(get<GetRouteByIdUseCase>()).isNotNull()
        assertThat(get<UpdateRouteUseCase>()).isNotNull()

        assertThat(get<CreateVehicleUseCase>()).isNotNull()
        assertThat(get<DeleteVehicleUseCase>()).isNotNull()
        assertThat(get<GetVehicleByIdUseCase>()).isNotNull()
        assertThat(get<UpdateVehicleUseCase>()).isNotNull()

        assertThat(get<CreateWarehouseUseCase>()).isNotNull()
        assertThat(get<DeleteWarehouseUseCase>()).isNotNull()
        assertThat(get<GetWarehouseByIdUseCase>()).isNotNull()
        assertThat(get<UpdateWarehouseUseCase>()).isNotNull()

        assertThat(get<AssignPackageToCargoQueueUseCase>()).isNotNull()
        assertThat(get<CalculateAveragePackageWeightUseCase>()).isNotNull()
        assertThat(get<FindPackagesByOriginUseCase>()).isNotNull()
        assertThat(get<FindPackagesForConsolidationUseCase>()).isNotNull()
        assertThat(get<ReroutePackageUseCase>()).isNotNull()

        assertThat(get<AddVehicleToHubUseCase>()).isNotNull()
        assertThat(get<AssignPackagesToVehicleUseCase>()).isNotNull()
        assertThat(get<DispatchVehicleUseCase>()).isNotNull()
        assertThat(get<FindStationedVehiclesByCapacityUseCase>()).isNotNull()
        assertThat(get<FindSuitableVehicleUseCase>()).isNotNull()
        assertThat(get<FindUnderutilizedVehiclesUseCase>()).isNotNull()
        assertThat(get<SuggestBestVehicleForPackageUseCase>()).isNotNull()

        assertThat(get<GetOverloadedWarehousesUseCase>()).isNotNull()
        assertThat(get<GetWarehouseLoadFactorUseCase>()).isNotNull()
        assertThat(get<TraceHubLineageUseCase>()).isNotNull()

        assertThat(get<FindOptimalPathUseCase>()).isNotNull()
        assertThat(get<FindFewestHopsRouteUseCase>()).isNotNull()
        assertThat(get<FindBidirectionalRouteUseCase>()).isNotNull()
        assertThat(get<CompareRoutingStrategiesUseCase>()).isNotNull()
    }

    @Test
    fun `should provide pricing engine required by calculate pricing use case`() {
        assertThat(get<RoutePricingEngine>()).isNotNull()
        assertThat(get<CalculatePricingUseCase>()).isNotNull()
    }
}
