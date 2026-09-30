package domain.usecase.crud.packages

import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.EntityValidationException
import domain.model.exception.OperationFailedException
import domain.model.exception.ResourceNotFoundException
import domain.model.input.UpdatePackageInput
import domain.model.validation.ValidationResult
import domain.repository.PackageRepository
import domain.validator.packages.PackageValidationError
import domain.validator.packages.UpdatePackageValidator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdatePackageUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()
    private val validator = mockk<UpdatePackageValidator>()

    private val useCase = UpdatePackageUseCase(
        packageRepository,
        validator
    )

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

    private val existingPackage = Package(
        id = "PKG-1",
        weight = 10.0,
        priority = Priority.URGENT,
        originHub = origin,
        destinationHub = destination
    )

    @Test
    fun `should update package successfully`() = runTest {
        // Given
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 20.0
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            packageRepository.getById("PKG-1")
        } returns existingPackage

        coEvery {
            packageRepository.update(any())
        } returns true

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(20.0, result.getOrNull()?.weight)
        assertEquals(existingPackage.priority, result.getOrNull()?.priority)

        coVerify(exactly = 1) {
            validator.validate(input)
            packageRepository.getById("PKG-1")
            packageRepository.update(any())
        }
    }

    @Test
    fun `should return EntityValidationException when validation fails`() = runTest {
        // Given
        val input = UpdatePackageInput(
            id = "PKG-1"
        )

        val violations = listOf(
            PackageValidationError.NoUpdateFields
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

        val validationException = exception as EntityValidationException
        assertEquals(violations, validationException.violations)

        coVerify(exactly = 1) {
            validator.validate(input)
        }

        coVerify(exactly = 0) {
            packageRepository.getById(any())
            packageRepository.update(any())
        }
    }

    @Test
    fun `should return ResourceNotFoundException when package does not exist`() = runTest {
        // Given
        val input = UpdatePackageInput(
            id = "PKG-999",
            weight = 20.0
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            packageRepository.getById("PKG-999")
        } returns null

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is ResourceNotFoundException
        )

        coVerify(exactly = 1) {
            packageRepository.getById("PKG-999")
        }

        coVerify(exactly = 0) {
            packageRepository.update(any())
        }
    }

    @Test
    fun `should return OperationFailedException when update returns false`() = runTest {
        // Given
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 20.0
        )

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            packageRepository.getById("PKG-1")
        } returns existingPackage

        coEvery {
            packageRepository.update(any())
        } returns false

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is OperationFailedException
        )

        coVerify(exactly = 1) {
            packageRepository.update(any())
        }
    }

    @Test
    fun `should return repository exception when fetching package fails`() = runTest {
        // Given
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 20.0
        )

        val exception = RuntimeException("Database error")

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            packageRepository.getById("PKG-1")
        } throws exception

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            packageRepository.getById("PKG-1")
        }

        coVerify(exactly = 0) {
            packageRepository.update(any())
        }
    }

    @Test
    fun `should return repository exception when updating package fails`() = runTest {
        // Given
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 20.0
        )

        val exception = RuntimeException("Database error")

        every {
            validator.validate(input)
        } returns ValidationResult.Valid

        coEvery {
            packageRepository.getById("PKG-1")
        } returns existingPackage

        coEvery {
            packageRepository.update(any())
        } throws exception

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            packageRepository.update(any())
        }
    }
}
