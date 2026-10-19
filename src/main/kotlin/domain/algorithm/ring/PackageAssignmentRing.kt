package domain.algorithm.ring

import domain.model.Vehicle
import java.util.NavigableMap
import java.util.TreeMap

class PackageAssignmentRing {

    private val ring: NavigableMap<Int, Vehicle> = TreeMap()

    fun isValidSlot(slot: Int): Boolean = slot in MIN_SLOT..MAX_SLOT

    fun addVehicle(slot: Int, vehicle: Vehicle) {
        require(isValidSlot(slot)) { "Invalid slot index: $slot" }
        ring[slot] = vehicle
    }

    fun removeVehicle(slot: Int) {
        ring.remove(slot)
    }

    fun getRingMap(): NavigableMap<Int, Vehicle> = ring

    private companion object {
        const val MIN_SLOT = 0
        const val MAX_SLOT = 99
    }
}
