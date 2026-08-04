package domain.ring

import java.util.TreeMap
import domain.model.Vehicle

object ClockwiseRouter {

    fun findResponsibleVehicle(ringMap: TreeMap<Int, Vehicle>, packageSlot: Int): Vehicle? {

        require(ringMap.isNotEmpty()) {
            "System Error: The Ring has no active vehicles!"
        }
        val tailMap = ringMap.tailMap(packageSlot)
        val assignedSlot = when {
            tailMap.isEmpty() -> ringMap.firstKey()
            else -> tailMap.firstKey()
        }

        return ringMap[assignedSlot]

    }
}
