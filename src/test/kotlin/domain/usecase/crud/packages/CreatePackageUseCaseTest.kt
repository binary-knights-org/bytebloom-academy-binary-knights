package domain.usecase.crud.packages

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.EntityValidationException
import domain.model.exception.InvalidPackageWeightException
import domain.model.exception.OperationFailedException
import domain.model.input.CreatePackageInput
import domain.model.validation.ValidationResult
import domain.repository.PackageRepository
import domain.validator.packages.CreatePackageValidator
import domain.validator.packages.PackageValidationError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreatePackageUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()
    private val validator = mockk<CreatePackageValidator>()

    private val useCase = CreatePackageUseCase(packageRepository, validator)

    private val origin = Warehouse(
        id = "WH-1",
        name = "Origin Warehouse",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )

    private val destination = Warehouse(
        id = "WH-2",
        name = "Destination Warehouse",
        regionalZone = RegionalZone.SOUTH,
        latitude = 31.50,
        longitude = 34.47
    )

    @Test
    fun `should create package successfully`() = runTest {
        // Given
        val input = CreatePackageInput(
            id = "PKG-1",
            weight = 10.0,
            priority = "URGENT",
            originHub = origin,
            destinationHub = destination
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            packageRepository.create(any())
        } returns true

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("PKG-1", result.getOrNull()?.id)
        assertEquals(10.0, result.getOrNull()?.weight)
        assertEquals(origin, result.getOrNull()?.originHub)
        assertEquals(destination, result.getOrNull()?.destinationHub)

        coVerify(exactly = 1) { packageRepository.create(any()) }
    }

    @Test
    fun `should return EntityValidationException when validation fails`() = runTest {
        // Given
        val input = CreatePackageInput(
            id = "PKG-1",
            weight = 0.0,
            priority = "URGENT",
            originHub = origin,
            destinationHub = destination
        )

        val violations = listOf(
            PackageValidationError.InvalidWeight
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Invalid(violations)

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)

        val exception = result.exceptionOrNull()
        assertTrue(exception is EntityValidationException)

        assertEquals(violations, exception.violations)

        coVerify(exactly = 1) {
            validator.validate(input)
        }

        coVerify(exactly = 0) {
            packageRepository.create(any())
        }
    }

    @Test
    fun `should return OperationFailedException when repository creation returns false`() = runTest {
        // Given
        val input = CreatePackageInput(
            id = "PKG-1",
            weight = 10.0,
            priority = "URGENT",
            originHub = origin,
            destinationHub = destination
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            packageRepository.create(any())
        } returns false

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is OperationFailedException
        )

        coVerify(exactly = 1) {
            packageRepository.create(any())
        }
    }

    @Test
    fun `should return repository exception when repository creation throws`() = runTest {
        // Given
        val input = CreatePackageInput(
            id = "PKG-1",
            weight = 10.0,
            priority = "URGENT",
            originHub = origin,
            destinationHub = destination
        )

        val exception = RuntimeException("Database error")

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            packageRepository.create(any())
        } throws exception

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            packageRepository.create(any())
        }
    }

    @Test
    fun `should return package model exception when validator incorrectly allows invalid weight`() = runTest {
        // Given
        val input = CreatePackageInput(
            id = "PKG-1",
            weight = 0.0,
            priority = "URGENT",
            originHub = origin,
            destinationHub = destination
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is InvalidPackageWeightException
        )

        coVerify(exactly = 0) {
            packageRepository.create(any())
        }
    }
}
