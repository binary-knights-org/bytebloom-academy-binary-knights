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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {

    singleOf(::provideCsvWarehouseDataSource) bind CsvWarehouseDataSource::class
    singleOf(::provideCsvPackageDataSource) bind CsvPackageDataSource::class
    singleOf(::provideCsvVehicleDataSource) bind CsvVehicleDataSource::class
    singleOf(::provideCsvRouteDataSource) bind CsvRouteDataSource::class

    singleOf(::RemoteDataSources)
    singleOf(::LocalDataSources)

    singleOf(::WarehouseRepositoryImpl) bind WarehouseRepository::class
    singleOf(::PackageRepositoryImpl) bind PackageRepository::class
    singleOf(::VehicleRepositoryImpl) bind VehicleRepository::class
    singleOf(::RouteRepositoryImpl) bind RouteRepository::class
}

private const val PACKAGE_FILE_PATH = "src/main/resources/packages.csv"
private const val WAREHOUSES_FILE_PATH = "src/main/resources/warehouses.csv"
private const val ROUTES_FILE_PATH = "src/main/resources/routes.csv"
private const val VEHICLES_FILE_PATH = "src/main/resources/fleet.csv"

private fun provideCsvWarehouseDataSource() = CsvWarehouseDataSourceImpl(CsvFileHandler(WAREHOUSES_FILE_PATH))
private fun provideCsvPackageDataSource() = CsvPackageDataSourceImpl(CsvFileHandler(PACKAGE_FILE_PATH))
private fun provideCsvVehicleDataSource() = CsvVehicleDataSourceImpl(CsvFileHandler(VEHICLES_FILE_PATH))
private fun provideCsvRouteDataSource() = CsvRouteDataSourceImpl(CsvFileHandler(ROUTES_FILE_PATH))
