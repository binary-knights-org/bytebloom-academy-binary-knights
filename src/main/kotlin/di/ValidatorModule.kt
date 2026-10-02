package di

import domain.validator.packages.CreatePackageValidator
import domain.validator.packages.UpdatePackageValidator
import domain.validator.routes.CreateRouteValidator
import domain.validator.routes.UpdateRouteValidator
import domain.validator.vehicle.CreateVehicleValidator
import domain.validator.vehicle.UpdateVehicleValidator
import domain.validator.warehouse.CreateWarehouseValidator
import domain.validator.warehouse.UpdateWarehouseValidator
import org.koin.dsl.module

val validatorModule = module {

    single { CreatePackageValidator() }
    single { UpdatePackageValidator() }

    single { CreateRouteValidator() }
    single { UpdateRouteValidator() }

    single { CreateVehicleValidator() }
    single { UpdateVehicleValidator() }

    single { CreateWarehouseValidator() }
    single { UpdateWarehouseValidator() }
}
