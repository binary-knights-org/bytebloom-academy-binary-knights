package domain.ring

import java.util.TreeMap
import domain.model.Vehicle

object ClockwiseRouter {

    fun findResponsibleVehicle(ring: TreeMap<Int, Vehicle>, slot: Int): Vehicle? {


        if (ring.isEmpty()) return null


        val vehiclesAhead = ring.tailMap(slot)


        return if (vehiclesAhead.isNotEmpty()) {
            val nearestSlot = vehiclesAhead.firstKey()
            ring[nearestSlot]
        } else {
            val wrapSlot = ring.firstKey()
            ring[wrapSlot]
        }
    }
}
