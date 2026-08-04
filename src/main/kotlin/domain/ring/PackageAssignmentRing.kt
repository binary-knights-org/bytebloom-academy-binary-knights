package domain.ring

import domain.model.Vehicle
import java.util.TreeMap

private const val MIN_SLOT = 0
private const val MAX_SLOT = 99

class PackageAssignmentRing {

    private val ring: TreeMap<Int,  Vehicle> = TreeMap()

    fun isValidSlot(slot: Int): Boolean {
        return slot in MIN_SLOT..MAX_SLOT
    }

    fun addVehicle(slot: Int, vehicle: Vehicle) {
        require(isValidSlot(slot))
        ring[slot] = vehicle
    }

    fun removeVehicle(slot: Int) {
        ring.remove(slot)
    }
    fun getRingMap(): TreeMap<Int, Vehicle> {
        return ring
    }
}
