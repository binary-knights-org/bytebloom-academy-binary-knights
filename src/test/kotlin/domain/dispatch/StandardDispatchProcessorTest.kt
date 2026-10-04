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
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StandardDispatchProcessorTest {

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
        priority: Priority = Priority.STANDARD
    ): Package {
        return Package(
            weight = weight,
            priority = priority,
            originHub = hub,
            destinationHub = destination
        )
    }

    private class RecordingDispatchProcessor : BaseDispatchProcessor() {
        val callOrder = mutableListOf<String>()

        override fun validateCargo(pkg: Package, vehicle: Vehicle) {
            callOrder.add("validateCargo")
        }

        override fun reserveVehicleCapacity(pkg: Package, vehicle: Vehicle) {
            callOrder.add("reserveVehicleCapacity")
        }

        override fun updateShipmentState(pkg: Package): Package {
            callOrder.add("updateShipmentState")
            return pkg
        }

        override fun notifyDispatchStatus(pkg: Package, vehicle: Vehicle): DispatchNotification? {
            callOrder.add("notifyDispatchStatus")
            return null
        }
    }

    private class FailingDispatchProcessor : BaseDispatchProcessor() {
        val callOrder = mutableListOf<String>()

        override fun validateCargo(pkg: Package, vehicle: Vehicle) {
            callOrder.add("validateCargo")
            throw InsufficientVehicleCapacityException("forced failure")
        }

        override fun reserveVehicleCapacity(pkg: Package, vehicle: Vehicle) {
            callOrder.add("reserveVehicleCapacity")
        }
    }

    @Test
    fun `dispatch should execute template steps in exact defined order`() {
        val processor = RecordingDispatchProcessor()
        val vehicle = createMockVehicle()
        val pkg = createPackage()

        processor.dispatch(pkg, vehicle)

        assertEquals(
            listOf("validateCargo", "reserveVehicleCapacity", "updateShipmentState", "notifyDispatchStatus"),
            processor.callOrder
        )
    }

    @Test
    fun `dispatch should stop execution immediately at validateCargo on failure`() {
        val processor = FailingDispatchProcessor()
        val vehicle = createMockVehicle()
        val pkg = createPackage()

        runCatching { processor.dispatch(pkg, vehicle) }

        assertEquals(listOf("validateCargo"), processor.callOrder)
    }

    @Test
    fun `dispatch should throw InsufficientVehicleCapacityException when validateCargo fails`() {
        val processor = FailingDispatchProcessor()
        val vehicle = createMockVehicle()
        val pkg = createPackage()

        assertFailsWith<InsufficientVehicleCapacityException> {
            processor.dispatch(pkg, vehicle)
        }
    }

    @Test
    fun `standard dispatch should load package to vehicle cargo on success`() {
        val processor = StandardDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 100.0, priority = Priority.STANDARD)

        processor.dispatch(pkg, vehicle)

        assertTrue(vehicle.loadedCargo.contains(pkg))
    }

    @Test
    fun `standard dispatch should update package state to AssignedToVehicleState on success`() {
        val processor = StandardDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 100.0, priority = Priority.STANDARD)

        processor.dispatch(pkg, vehicle)

        assertTrue(pkg.getState() is AssignedToVehicleState)
    }

    @Test
    fun `standard dispatch should throw InsufficientVehicleCapacityException when weight exceeds capacity`() {
        val processor = StandardDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 50.0)
        val pkg = createPackage(weight = 100.0, priority = Priority.STANDARD)

        assertFailsWith<InsufficientVehicleCapacityException> {
            processor.dispatch(pkg, vehicle)
        }
    }

    @Test
    fun `standard dispatch should not add package to vehicle when weight exceeds capacity`() {
        val processor = StandardDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 50.0)
        val pkg = createPackage(weight = 100.0, priority = Priority.STANDARD)

        runCatching { processor.dispatch(pkg, vehicle) }

        assertTrue(vehicle.loadedCargo.isEmpty())
    }

    @Test
    fun `standard dispatch should keep package state as CreatedState when weight exceeds capacity`() {
        val processor = StandardDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 50.0)
        val pkg = createPackage(weight = 100.0, priority = Priority.STANDARD)

        runCatching { processor.dispatch(pkg, vehicle) }

        assertTrue(pkg.getState() is CreatedState)
    }

    @Test
    fun `standard dispatch should throw InvalidDispatchPriorityException when priority is not STANDARD`() {
        val processor = StandardDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 10.0, priority = Priority.URGENT)

        assertFailsWith<InvalidDispatchPriorityException> {
            processor.dispatch(pkg, vehicle)
        }
    }

    @Test
    fun `standard dispatch should not add package to vehicle when priority is invalid`() {
        val processor = StandardDispatchProcessor()
        val vehicle = createMockVehicle(maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 10.0, priority = Priority.URGENT)

        runCatching { processor.dispatch(pkg, vehicle) }

        assertTrue(vehicle.loadedCargo.isEmpty())
    }

    @Test
    fun `standard dispatch should keep package state as CreatedState when priority is invalid`() {
        val processor = StandardDispatchProcessor()
        val vehicle = createMockVehicle(id = "TRK-1", maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 10.0, priority = Priority.URGENT)

        runCatching { processor.dispatch(pkg, vehicle) }

        assertTrue(pkg.getState() is CreatedState)
    }

    @Test
    fun `standard dispatch notification should return null on success`() {
        val processor = StandardDispatchProcessor()
        val vehicle = createMockVehicle(id = "TRK-1", maxCapacityKg = 1000.0)
        val pkg = createPackage(weight = 10.0, priority = Priority.STANDARD)

        val notification = processor.dispatch(pkg, vehicle)

        assertNull(notification)
    }
}
