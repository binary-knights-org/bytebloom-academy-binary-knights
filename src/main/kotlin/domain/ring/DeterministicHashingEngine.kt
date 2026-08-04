package domain.ring

import kotlin.math.abs
import domain.model.Package

    private const val RING_SIZE = 100

    object DeterministicHashingEngine {

        fun calculateSlot(pkg: Package): Int {
            val hashCode: Int = pkg.id.hashCode()
            val positiveHash: Int = abs(hashCode)
            val slot: Int = positiveHash % RING_SIZE

            return slot
        }
}
