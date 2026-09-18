package domain.model

import domain.exception.EntityValidationException
import java.util.UUID

class Package private constructor(
     val id: String,
     val weight: Double,
     val priority: String,
     val originHub: Warehouse,
     var destinationHub: Warehouse
) {
     companion object {
          const val ID_PREFIX = "PKG-"
          const val MIN_WEIGHT = 0.0
          val ALLOWED_PRIORITIES = setOf("URGENT", "STANDARD", "LOW")

          fun isValidId(id: String): Boolean {
               if (id.isBlank()) return false
               val hasValidPrefix = id.startsWith(ID_PREFIX)
               val isUuid = runCatching { UUID.fromString(id) }.isSuccess
               return hasValidPrefix || isUuid
          }

          fun isValidWeight(weight: Double): Boolean = weight > MIN_WEIGHT
          fun isValidPriority(priority: String): Boolean = priority.uppercase() in ALLOWED_PRIORITIES

          fun create(
               id: String,
               weight: Double,
               priority: String,
               originHub: Warehouse,
               destinationHub: Warehouse
          ): Package {
               val validationError = when {
                    !isValidId(id) ->
                         "Invalid Package ID format (Must start with PKG- or be a valid UUID)."

                    !isValidWeight(weight) ->
                         "Weight must be greater than $MIN_WEIGHT."

                    !isValidPriority(priority) ->
                         "Invalid priority value."

                    else -> null
               }

               if (validationError != null) {
                    throw EntityValidationException(validationError)
               }
               return Package(id, weight, priority.uppercase(), originHub, destinationHub)
          }
     }
}
