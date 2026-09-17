package domain.validator.warehouse

import domain.model.Warehouse
import domain.validator.IdValidator

object GeoConstraints {
    const val MIN_LATITUDE = -90.0
    const val MAX_LATITUDE = 90.0
    const val MIN_LONGITUDE = -180.0
    const val MAX_LONGITUDE = 180.0
    const val NULL_ISLAND_COORDINATE = 0.0

    val LATITUDE_RANGE = MIN_LATITUDE..MAX_LATITUDE
    val LONGITUDE_RANGE = MIN_LONGITUDE..MAX_LONGITUDE
}


class ValidateCreateWarehouseUseCase {

    operator fun invoke(warehouse: Warehouse): ValidationResult {
        val builder = ValidationResultBuilder()

        validateApplicationLevel(warehouse, builder)

        validateBusinessLevel(warehouse, builder)

        return builder.build()
    }

    private fun validateApplicationLevel(warehouse: Warehouse, builder: ValidationResultBuilder) {
        val idResult = IdValidator.validateWarehouseId(warehouse.id, "id")
        idResult.errorsOrNull()?.let { builder.addViolations(it) }

        builder.check(warehouse.name.isNotBlank(), "name", "Warehouse name must not be blank")
        builder.check(warehouse.regionalZone.isNotBlank(), "regionalZone", "Regional zone must not be blank")
        builder.check(
            warehouse.latitude in GeoConstraints.LATITUDE_RANGE,
            "latitude",
            "Latitude must be between -90.0 and 90.0, received ${warehouse.latitude}"
        )
        builder.check(
            warehouse.longitude in GeoConstraints.LONGITUDE_RANGE,
            "longitude",
            "Longitude must be between -180.0 and 180.0, received ${warehouse.longitude}"
        )
    }

    private fun validateBusinessLevel(warehouse: Warehouse, builder: ValidationResultBuilder) {
        builder.check(
            !(warehouse.latitude == GeoConstraints.NULL_ISLAND_COORDINATE &&
                    warehouse.longitude == GeoConstraints.NULL_ISLAND_COORDINATE),
            "coordinates",
            "Warehouse cannot be placed at null island (0.0, 0.0)"
        )
    }
}

