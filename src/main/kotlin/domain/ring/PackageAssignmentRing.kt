package domain.ring

import java.util.TreeMap

private const val MIN_SLOT = 0
private const val MAX_SLOT = 99

class PackageAssignmentRing {

    private val ring: TreeMap<Int, String> = TreeMap()

    fun isValidSlot(slot: Int): Boolean {
        return slot in MIN_SLOT..MAX_SLOT
    }

    fun addVehicle(slot: Int, vehicleId: String) {
        require(isValidSlot(slot))
        ring[slot] = vehicleId
    }

    fun removeVehicle(slot: Int) {
        ring.remove(slot)
    }
}
