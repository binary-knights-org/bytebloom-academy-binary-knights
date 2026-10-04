package domain.dispatch

import domain.model.Package
import domain.model.Priority
import domain.model.Vehicle
import domain.model.Warehouse
import domain.model.exception.InsufficientVehicleCapacityException
import domain.model.exception.InvalidDispatchPriorityException
import domain.state.AssignedToVehicleState
import domain.state.CreatedState
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ExpressDispatchProcessorTest {

    private val hub = mockk<Warehouse> {
        every { id } returns "WH-1"
        every { name } returns "Main Hub"
    }

    private val destination = mockk<Warehouse> {
        every { id } returns "WH-2"
        every { name } returns "Destination Hub"
    }

    private fun createMockVehicle(
        id: String = "TRK-1",
        maxCapacityKg: Double = 1000.0,
        loadedCargo: MutableList<Package> = mutableListOf()
    ): Vehicle {
        val vehicle = mockk<Vehicle>()
        every { vehicle.id } returns id
        every { vehicle.maxCapacityKg } returns maxCapacityKg
        every { vehicle.loadedCargo } returns loadedCargo
        every { vehicle.currentLoadKg } answers { loadedCargo.sumOf { it.weight } }
        every { vehicle.loadPackage(any()) } answers {
            val pkg = firstArg<Package>()
            loadedCargo.add(pkg)
            true
        }
        return vehicle
    }

    private fun createPackage(
        weight: Double = 10.0,
        priority: Priority = Priority.URGENT
    ): Package {
        return Package(
            weight = weight,
            priority = priority,
            originHub = hub,
            destinationHub = destination
        )
    }

    @Test
    fun `express dispatch should add package to vehicle cargo on success`() {
        val processor = ExpressDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 100.0, priority = Priority.URGENT)

        processor.dispatch(pkg, vehicle)

        assertTrue(vehicle.loadedCargo.contains(pkg))
    }

    @Test
    fun `express dispatch should update package state to AssignedToVehicleState on success`() {
        val processor = ExpressDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 100.0, priority = Priority.URGENT)

        processor.dispatch(pkg, vehicle)

        assertTrue(pkg.getState() is AssignedToVehicleState)
    }

    @Test
    fun `express dispatch should throw InsufficientVehicleCapacityException when weight exceeds capacity`() {
        val processor = ExpressDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 50.0)
        val pkg = createPackage(weight = 100.0, priority = Priority.URGENT)

        assertFailsWith<InsufficientVehicleCapacityException> {
            processor.dispatch(pkg, vehicle)
        }
    }

    @Test
    fun `express dispatch should not add package to vehicle when weight exceeds capacity`() {
        val processor = ExpressDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 50.0)
        val pkg = createPackage(weight = 100.0, priority = Priority.URGENT)

        runCatching { processor.dispatch(pkg, vehicle) }

        assertTrue(vehicle.loadedCargo.isEmpty())
    }

    @Test
    fun `express dispatch should keep package state as CreatedState when weight exceeds capacity`() {
        val processor = ExpressDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 50.0)
        val pkg = createPackage(weight = 100.0, priority = Priority.URGENT)

        runCatching { processor.dispatch(pkg, vehicle) }

        assertTrue(pkg.getState() is CreatedState)
    }

    @Test
    fun `express dispatch should throw InvalidDispatchPriorityException when priority is not URGENT`() {
        val processor = ExpressDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 10.0, priority = Priority.STANDARD)

        assertFailsWith<InvalidDispatchPriorityException> {
            processor.dispatch(pkg, vehicle)
        }
    }

    @Test
    fun `express dispatch should not add package to vehicle when priority is invalid`() {
        val processor = ExpressDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 10.0, priority = Priority.STANDARD)

        runCatching { processor.dispatch(pkg, vehicle) }

        assertTrue(vehicle.loadedCargo.isEmpty())
    }

    @Test
    fun `express dispatch should keep package state as CreatedState when priority is invalid`() {
        val processor = ExpressDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 10.0, priority = Priority.STANDARD)

        runCatching { processor.dispatch(pkg, vehicle) }

        assertTrue(pkg.getState() is CreatedState)
    }

    @Test
    fun `express dispatch notification should contain correct package id`() {
        val processor = ExpressDispatchProcessor()
        val vehicle = createMockVehicle(id = "TRK-1", maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 10.0, priority = Priority.URGENT)

        val notification = processor.dispatch(pkg, vehicle)

        assertEquals(pkg.id, notification?.packageId)
    }

    @Test
    fun `express dispatch notification should contain correct vehicle id`() {
        val processor = ExpressDispatchProcessor()
        val vehicle = createMockVehicle(id = "TRK-1", maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 10.0, priority = Priority.URGENT)

        val notification = processor.dispatch(pkg, vehicle)

        assertEquals("TRK-1", notification?.vehicleId)
    }
}
