package domain.algoritm.dp

import com.google.common.truth.Truth.assertThat
import domain.algorithm.dp.KnapsackCargoOptimizer
import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Warehouse
import org.junit.jupiter.api.Test

class KnapsackCargoOptimizerTest {

    private val knapsackCargoOptimizer = KnapsackCargoOptimizer()

    private val origin = Warehouse(
        id = "WH-1",
        name = "Origin Warehouse",
        regionalZone = RegionalZone.NORTH,
        latitude = 31.95,
        longitude = 35.91
    )

    private val destination = Warehouse(
        id = "WH-2",
        name = "Destination Warehouse",
        regionalZone = RegionalZone.SOUTH,
        latitude = 31.50,
        longitude = 34.47
    )

    private val packageA = Package(
        id = "A",
        weight = 6.0,
        priority = Priority.URGENT,
        originHub = origin,
        destinationHub = destination
    )

    private val packageB = Package(
        id = "B",
        weight = 3.0,
        priority = Priority.STANDARD,
        originHub = origin,
        destinationHub = destination
    )

    private val packageC = Package(
        id = "C",
        weight = 4.0,
        priority = Priority.URGENT,
        originHub = origin,
        destinationHub = destination
    )

    private val packageD = Package(
        id = "D",
        weight = 2.0,
        priority = Priority.LOW,
        originHub = origin,
        destinationHub = destination
    )

    private val packageF = Package(
        id = "F",
        weight = 2.0,
        priority = Priority.URGENT,
        originHub = origin,
        destinationHub = destination,
        volumeM3 = 8.0
    )

    private val packageE = Package(
        id = "E",
        weight = 8.0,
        priority = Priority.URGENT,
        originHub = origin,
        destinationHub = destination,
        volumeM3 = 2.0
    )

    private val packages = listOf(packageA, packageB, packageC, packageD)

    @Test
    fun `should select package combination with maximum priority without exceeding capacity`() {
        val result = knapsackCargoOptimizer(
            packages = packages,
            maxCapacityKg = 10.0
        )

        assertThat(result).containsExactly(packageA,packageC)
    }

    @Test
    fun `should select  package E when  package F exceeds volume constraint`() {
        val result = knapsackCargoOptimizer.optimize2D(
            packages = listOf(packageF, packageE),
            maxCapacityKg = 10.0,
            maxVolumeM3 = 8.0
        )

        assertThat(result).containsExactly(packageE)
    }

    @Test
    fun `should select both packages when only weight constraint is applied`() {
        val packages = listOf(packageF, packageE)
        val weightOnlyResult = knapsackCargoOptimizer(
            packages = packages,
            maxCapacityKg = 10.0
        )

        assertThat(weightOnlyResult).containsExactly(packageF, packageE)
    }
}
