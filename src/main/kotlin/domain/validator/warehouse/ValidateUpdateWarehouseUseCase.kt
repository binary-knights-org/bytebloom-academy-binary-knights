package domain.validator.warehouse

import domain.model.Warehouse
import domain.validator.IdValidator

data class UpdateWarehouseParams(
    val id: String,
    val name: String? = null,
    val regionalZone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

class ValidateUpdateWarehouseUseCase {

    operator fun invoke(params: UpdateWarehouseParams): ValidationResult {
        val builder = ValidationResultBuilder()

        val idResult = IdValidator.validateWarehouseId(params.id, "id")
        idResult.errorsOrNull()?.let { builder.addViolations(it) }

        val hasAtLeastOneField = params.name != null ||
                params.regionalZone != null ||
                params.latitude != null ||
                params.longitude != null

        builder.check(
            hasAtLeastOneField,
            "targetProperties",
            "At least one target property must be populated for update"
        )

        params.name?.let {
            builder.check(it.isNotBlank(), "name", "Updated name must not be blank")
        }
        params.regionalZone?.let {
            builder.check(it.isNotBlank(), "regionalZone", "Updated regional zone must not be blank")
        }
        params.latitude?.let {
            builder.check(
                it in GeoConstraints.LATITUDE_RANGE,
                "latitude",
                "Updated latitude must be between -90.0 and 90.0"
            )
        }
        params.longitude?.let {
            builder.check(
                it in GeoConstraints.LONGITUDE_RANGE,
                "longitude",
                "Updated longitude must be between -180.0 and 180.0"
            )
        }

        return builder.build()
    }

    operator fun invoke(warehouse: Warehouse): ValidationResult {
        return invoke(
            UpdateWarehouseParams(
                id = warehouse.id,
                name = warehouse.name,
                regionalZone = warehouse.regionalZone,
                latitude = warehouse.latitude,
                longitude = warehouse.longitude
            )
        )
    }
}
