package domain

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.exception.ResourceNotFoundException
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse
import domain.model.input.CreateWarehouseInput
import domain.model.input.UpdateWarehouseInput
import domain.repository.WarehouseRepository
import domain.usecase.crud.warehouse.CreateWarehouseUseCase
import domain.usecase.crud.warehouse.GetWarehouseByIdUseCase
import domain.usecase.crud.warehouse.UpdateWarehouseUseCase
import domain.validator.warehouse.CreateWarehouseValidator
import domain.validator.warehouse.UpdateWarehouseValidator
import domain.validator.warehouse.WarehouseIdValidator
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ErrorStrategyTest {

    @Test
    fun `Warehouse creation accumulates all violations when multiple fields are invalid`() {
        // ID invalid, name blank, regionalZone blank, latitude out of range, longitude out of range
        val result = Warehouse.create(
            id = "INVALID-ID",
            name = "",
            regionalZone = "   ",
            latitude = 95.0,
            longitude = -195.0
        )

        assertTrue(result.isFailure, "Result should be failure")
        val exception = result.exceptionOrNull()
        assertTrue(exception is EntityValidationException, "Exception must be EntityValidationException")

        val violations = exception.violations
        // Should contain violations for id, name, regionalZone, latitude, longitude
        val fieldNames = violations.map { it.field }.toSet()
        assertTrue("id" in fieldNames, "Should have violation for id")
        assertTrue("name" in fieldNames, "Should have violation for name")
        assertTrue("regionalZone" in fieldNames, "Should have violation for regionalZone")
        assertTrue("latitude" in fieldNames, "Should have violation for latitude")
        assertTrue("longitude" in fieldNames, "Should have violation for longitude")
        assertTrue(violations.size >= 5, "Must accumulate all 5 violations")
    }

    @Test
    fun `Direct Warehouse constructor throws EntityValidationException with all violations`() {
        var caughtException: EntityValidationException? = null
        try {
            Warehouse(
                id = "INVALID-PREFIX",
                name = "",
                regionalZone = "",
                latitude = 100.0,
                longitude = 200.0
            )
        } catch (e: EntityValidationException) {
            caughtException = e
        }

        assertTrue(caughtException != null, "Should throw EntityValidationException")
        assertTrue(caughtException.violations.size >= 5, "Should have accumulated all errors")
    }

    @Test
    fun `CreateWarehouseValidator accumulates all violations into ValidationWarehouseResult`() {
        val validator = CreateWarehouseValidator(WarehouseIdValidator())
        val input = CreateWarehouseInput(
            id = "BAD-ID",
            name = "",
            regionalZone = "",
            latitude = 100.0,
            longitude = -200.0
        )

        val result = validator.validate(input)
        assertTrue(result.isInvalid, "Validation must be invalid")
        val errors = result.errorsOrNull().orEmpty()
        val fields = errors.map { it.field }.toSet()
        assertTrue("id" in fields)
        assertTrue("name" in fields)
        assertTrue("regionalZone" in fields)
        assertTrue("latitude" in fields)
        assertTrue("longitude" in fields)
    }

    @Test
    fun `CreateWarehouseUseCase returns Result failure with EntityValidationException on invalid input`() = runBlocking {
        val fakeRepo = object : FakeWarehouseRepository() {}
        val useCase = CreateWarehouseUseCase(fakeRepo, CreateWarehouseValidator(WarehouseIdValidator()))

        val input = CreateWarehouseInput(
            id = "INVALID_ID",
            name = "",
            regionalZone = "",
            latitude = 95.0,
            longitude = -200.0
        )

        val result = useCase(input)
        assertTrue(result.isFailure)

        var handledWithFailure = false
        result.onFailure { error ->
            handledWithFailure = true
            assertTrue(error is EntityValidationException)
            assertTrue(error.violations.size >= 5)
        }
        assertTrue(handledWithFailure)
    }

    @Test
    fun `CreateWarehouseUseCase returns Result success on valid input`() = runBlocking {
        val fakeRepo = object : FakeWarehouseRepository() {
            override suspend fun create(item: Warehouse): Boolean = true
        }
        val useCase = CreateWarehouseUseCase(fakeRepo, CreateWarehouseValidator(WarehouseIdValidator()))

        val input = CreateWarehouseInput(
            id = "WH-12345",
            name = "North Logistics Hub",
            regionalZone = "Zone A",
            latitude = 25.0,
            longitude = 45.0
        )

        val result = useCase(input)
        assertTrue(result.isSuccess)
        var receivedWarehouse: Warehouse? = null
        result.onSuccess { warehouse ->
            receivedWarehouse = warehouse
            assertEquals("WH-12345", warehouse.id)
            assertEquals("North Logistics Hub", warehouse.name)
        }
        assertTrue(receivedWarehouse != null)
    }

    @Test
    fun `GetWarehouseByIdUseCase returns ResourceNotFoundException when entity not found`() = runBlocking {
        val fakeRepo = object : FakeWarehouseRepository() {
            override suspend fun getById(id: String): Warehouse? = null
        }
        val useCase = GetWarehouseByIdUseCase(fakeRepo, WarehouseIdValidator())

        val result = useCase("WH-99999")
        assertTrue(result.isFailure)
        result.onFailure { error ->
            assertTrue(error is ResourceNotFoundException)
        }
    }

    @Test
    fun `CreateWarehouseUseCase returns DatabaseConflictException when database fails`() = runBlocking {
        val fakeRepo = object : FakeWarehouseRepository() {
            override suspend fun create(item: Warehouse): Boolean = false
        }
        val useCase = CreateWarehouseUseCase(fakeRepo, CreateWarehouseValidator(WarehouseIdValidator()))

        val input = CreateWarehouseInput(
            id = "WH-12345",
            name = "North Hub",
            regionalZone = "Zone A",
            latitude = 10.0,
            longitude = 20.0
        )

        val result = useCase(input)
        assertTrue(result.isFailure)
        result.onFailure { error ->
            assertTrue(error is DatabaseConflictException)
        }
    }
}

open class FakeWarehouseRepository : WarehouseRepository {
    override suspend fun getAll(): List<Warehouse> = emptyList()
    override suspend fun getById(id: String): Warehouse? = null
    override suspend fun create(item: Warehouse): Boolean = true
    override suspend fun update(item: Warehouse): Boolean = true
    override suspend fun delete(id: String): Boolean = true
}
