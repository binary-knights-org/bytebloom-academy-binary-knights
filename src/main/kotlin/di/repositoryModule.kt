package di

import data.local.csv.CsvFileHandler
import data.local.csv.CsvPackageDataSourceImpl
import data.local.csv.CsvRouteDataSourceImpl
import data.local.csv.CsvVehicleDataSourceImpl
import data.local.csv.CsvWarehouseDataSourceImpl
import data.local.datasource.CsvPackageDataSource
import data.local.datasource.CsvRouteDataSource
import data.local.datasource.CsvVehicleDataSource
import data.local.datasource.CsvWarehouseDataSource
import data.repository.LocalDataSources
import data.repository.PackageRepositoryImpl
import data.repository.RemoteDataSources
import data.repository.RouteRepositoryImpl
import data.repository.VehicleRepositoryImpl
import data.repository.WarehouseRepositoryImpl
import domain.repository.PackageRepository
import domain.repository.RouteRepository
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import org.koin.dsl.module

private const val PACKAGE_FILE_PATH = "src/main/resources/packages.csv"
private const val WAREHOUSES_FILE_PATH = "src/main/resources/warehouses.csv"
private const val ROUTES_FILE_PATH = "src/main/resources/routes.csv"
private const val VEHICLES_FILE_PATH = "src/main/resources/fleet.csv"

val repositoryModule = module {

    
    single<CsvWarehouseDataSource> {
        CsvWarehouseDataSourceImpl(CsvFileHandler(WAREHOUSES_FILE_PATH))
    }
    single<CsvPackageDataSource> {
        CsvPackageDataSourceImpl(CsvFileHandler(PACKAGE_FILE_PATH))
    }
    single<CsvVehicleDataSource> {
        CsvVehicleDataSourceImpl(CsvFileHandler(VEHICLES_FILE_PATH))
    }
    single<CsvRouteDataSource> {
        CsvRouteDataSourceImpl(CsvFileHandler(ROUTES_FILE_PATH))
    }

    
    single {
        RemoteDataSources(
            warehouse = get(),
            packageSource = get(),
            vehicle = get(),
            route = get()
        )
    }
    single {
        LocalDataSources(
            warehouse = get(),
            packageSource = get(),
            vehicle = get(),
            route = get()
        )
    }

  
    single<WarehouseRepository> {
        WarehouseRepositoryImpl(
            remoteSources = get(),
            localSources = get()
        )
    }

    single<PackageRepository> {
        PackageRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            warehouseRepository = get()
        )
    }

    single<VehicleRepository> {
        VehicleRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            warehouseRepository = get()
        )
    }

    single<RouteRepository> {
        RouteRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            warehouseRepository = get()
        )
    }
}
