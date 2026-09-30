package domain.usecase.crud.warehouse

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.EntityValidationException
import domain.model.exception.OperationFailedException
import domain.model.input.CreateWarehouseInput
import domain.model.validation.ValidationResult
import domain.repository.WarehouseRepository
import domain.validator.warehouse.CreateWarehouseValidator
import domain.validator.warehouse.WarehouseValidationError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CreateWarehouseUseCaseTest {

    private val warehouseRepository = mockk<WarehouseRepository>()
    private val validator = mockk<CreateWarehouseValidator>()

    private val useCase = CreateWarehouseUseCase(
        warehouseRepository = warehouseRepository,
        validator = validator
    )

    private val validInput = CreateWarehouseInput(
        id = "WH-001",
        name = "Hub-001",
        regionalZone = "CENTRAL",
        latitude = 37.91,
        longitude = -88.46
    )

    private val expectedWarehouse = Warehouse(
        id = "WH-001",
        name = "Hub-001",
        regionalZone = RegionalZone.CENTRAL,
        latitude = 37.91,
        longitude = -88.46
    )

    @Test
    fun `should create warehouse successfully for valid input`() = runTest {
        // Given
        every {
            validator.validate(validInput)
        } returns ValidationResult.Valid

        coEvery {
            warehouseRepository.create(expectedWarehouse)
        } returns true

        // When
        val result = useCase(validInput)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedWarehouse, result.getOrNull())

        coVerify(exactly = 1) {
            validator.validate(validInput)
            warehouseRepository.create(expectedWarehouse)
        }
    }

    @Test
    fun `should return entity validation failure when input is invalid`() = runTest {
        // Given
        val violations = listOf(
            WarehouseValidationError.BlankName
        )

        every {
            validator.validate(validInput)
        } returns ValidationResult.Invalid(violations)

        // When
        val result = useCase(validInput)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is EntityValidationException
        )

        coVerify(exactly = 1) {
            validator.validate(validInput)
        }

        coVerify(exactly = 0) {
            warehouseRepository.create(any())
        }
    }

    @Test
    fun `should return failure when regional zone is invalid`() = runTest {
        // Given
        val input = validInput.copy(
            regionalZone = "INVALID_ZONE"
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is IllegalArgumentException
        )

        coVerify(exactly = 1) {
            validator.validate(input)
        }

        coVerify(exactly = 0) {
            warehouseRepository.create(any())
        }
    }

    @Test
    fun `should return operation failed exception when repository creation returns false`() = runTest {
        // Given
        every {
            validator.validate(validInput)
        } returns ValidationResult.Valid

        coEvery {
            warehouseRepository.create(expectedWarehouse)
        } returns false

        // When
        val result = useCase(validInput)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is OperationFailedException
        )

        coVerify(exactly = 1) {
            validator.validate(validInput)
            warehouseRepository.create(expectedWarehouse)
        }
    }

    @Test
    fun `should return repository exception when repository creation throws`() = runTest {
        // Given
        val exception = RuntimeException("Database error")

        every {
            validator.validate(validInput)
        } returns ValidationResult.Valid

        coEvery {
            warehouseRepository.create(expectedWarehouse)
        } throws exception

        // When
        val result = useCase(validInput)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            validator.validate(validInput)
            warehouseRepository.create(expectedWarehouse)
        }
    }
}
