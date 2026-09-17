package domain.validator.warehouse

// المفروض يكون هذا الملف خارج الwarehouse
// بس عشان ما يتعارض مع الي استخدمت وه فا حاليًا هنا

data class FieldViolation(
    val field: String,
    val message: String
)

sealed class ValidationResult {
    data object Valid : ValidationResult()

    data class Invalid(val violations: List<FieldViolation>) : ValidationResult() {
        constructor(field: String, message: String) : this(listOf(FieldViolation(field, message)))
    }

    val isValid: Boolean get() = this is Valid

    val isInvalid: Boolean get() = this is Invalid

    fun errorsOrNull(): List<FieldViolation>? = (this as? Invalid)?.violations
}

class ValidationResultBuilder {
    private val violations = mutableListOf<FieldViolation>()

    fun addViolation(field: String, message: String): ValidationResultBuilder {
        violations.add(FieldViolation(field, message))
        return this
    }

    fun addViolations(newViolations: List<FieldViolation>): ValidationResultBuilder {
        violations.addAll(newViolations)
        return this
    }

    fun check(condition: Boolean, field: String, message: String): ValidationResultBuilder {
        if (!condition) {
            violations.add(FieldViolation(field, message))
        }
        return this
    }

    fun build(): ValidationResult {
        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }
}
