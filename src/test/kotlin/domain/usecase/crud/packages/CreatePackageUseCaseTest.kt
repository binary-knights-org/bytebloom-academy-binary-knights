package domain.usecase.crud.packages

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.input.CreatePackageInput
import domain.repository.PackageRepository
import domain.validator.ValidationResult
import domain.validator.packages.CreatePackageValidator
import domain.validator.packages.PackageValidationError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class CreatePackageUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()
    private val validator = mockk<CreatePackageValidator>()

    private val useCase = CreatePackageUseCase(
        packageRepository, validator
    )

    private val origin = Warehouse(
        id = "WH-1", name = "Origin Warehouse", regionalZone = RegionalZone.NORTH, latitude = 31.95, longitude = 35.91
    )

    private val destination = Warehouse(
        id = "WH-2",
        name = "Destination Warehouse",
        regionalZone = RegionalZone.SOUTH,
        latitude = 31.50,
        longitude = 34.47
    )

    @Test
    fun `should create package successfully`() = runBlocking {

        // Given
        val input = CreatePackageInput(
            id = "PKG-1", weight = 10.0, priority = "URGENT", originHub = origin, destinationHub = destination
        )
        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { packageRepository.create(any()) } returns true

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { packageRepository.create(any()) }
    }

    @Test
    fun `should fail when package creation fails`() = runBlocking {

        // Given
        val input = CreatePackageInput(
            id = "PKG-1", weight = 10.0, priority = "URGENT", originHub = origin, destinationHub = destination
        )
        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { packageRepository.create(any()) } returns false

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 1) { packageRepository.create(any()) }
    }

    @Test
    fun `should fail validation when weight is invalid`() = runBlocking {

        // Given
        val input = CreatePackageInput(
            id = "PKG-1", weight = 0.0, priority = "URGENT", originHub = origin, destinationHub = destination
        )
        every { validator.validate(input) } returns ValidationResult.Invalid(
            listOf(PackageValidationError.InvalidWeight)
        )

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { packageRepository.create(any()) }
    }

    @Test
    fun `should fail validation when origin and destination are the same`() = runBlocking {

        // Given
        val input = CreatePackageInput(
            id = "PKG-1", weight = 10.0, priority = "URGENT", originHub = origin, destinationHub = origin
        )
        every { validator.validate(input) } returns ValidationResult.Invalid(
            listOf(PackageValidationError.SameOriginAndDestination)
        )

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { packageRepository.create(any()) }
    }
}
