package domain.usecase.crud.packages

import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.input.UpdatePackageInput
import domain.repository.PackageRepository
import domain.validator.ValidationResult
import domain.validator.packages.UpdatePackageValidator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
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
    fun `should update package successfully`() = runBlocking {

        // Given
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 20.0
        )

        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { packageRepository.getById("PKG-1") } returns existingPackage
        coEvery { packageRepository.update(any()) } returns true

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(20.0, result.getOrNull()?.weight)

        coVerify(exactly = 1) { packageRepository.getById("PKG-1") }
        coVerify(exactly = 1) { packageRepository.update(any()) }
    }

    @Test
    fun `should fail when package does not exist`() = runBlocking {

        // Given
        val input = UpdatePackageInput(
            id = "PKG-999",
            weight = 20.0
        )

        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { packageRepository.getById("PKG-999") } returns null

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)

        coVerify(exactly = 1) { packageRepository.getById("PKG-999") }
        coVerify(exactly = 0) { packageRepository.update(any()) }
    }

    @Test
    fun `should fail when validation fails`() = runBlocking {

        // Given
        val input = UpdatePackageInput(
            id = "PKG-1"
        )

        every { validator.validate(input) } returns ValidationResult.Invalid(emptyList())

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)

        coVerify(exactly = 0) { packageRepository.getById(any()) }
        coVerify(exactly = 0) { packageRepository.update(any()) }
    }

    @Test
    fun `should fail when package update returns false`() = runBlocking {

        // Given
        val input = UpdatePackageInput(
            id = "PKG-1",
            weight = 20.0
        )

        every { validator.validate(input) } returns ValidationResult.Valid
        coEvery { packageRepository.getById("PKG-1") } returns existingPackage
        coEvery { packageRepository.update(any()) } returns false

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)

        coVerify(exactly = 1) { packageRepository.update(any()) }
    }
}
