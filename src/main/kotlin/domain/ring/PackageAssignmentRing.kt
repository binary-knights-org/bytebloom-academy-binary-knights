package domain.ring

import java.util.TreeMap
import kotlin.math.abs

private const val MIN_SLOT = 0
private const val MAX_SLOT = 99
private const val RING_SIZE = 100

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

    fun getSlot(packageId: String): Int {
        val hashCode: Int = packageId.hashCode()
        val positiveHash: Int = abs(hashCode)
        val slot: Int = positiveHash % RING_SIZE
        return slot
    }

}
