package domain.algoritm.dp

import com.google.common.truth.Truth.assertThat
import domain.algorithm.dp.KnapsackCargoOptimizer
import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.InvalidCargoInputException
import org.junit.Assert.assertThrows
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

    @Test
    fun `should return empty list when no packages are provided`() {
        val result = knapsackCargoOptimizer(
            packages = emptyList(),
            maxCapacityKg = 10.0
        )

        assertThat(result).isEmpty()
    }

    @Test
    fun `should return empty list when capacity is zero and no packages can fit`() {
        val result = knapsackCargoOptimizer(
            packages = packages,
            maxCapacityKg = 0.0
        )

        assertThat(result).isEmpty()
    }

    @Test
    fun `should reject negative weight capacity`() {
        assertThrows(InvalidCargoInputException::class.java) {
            knapsackCargoOptimizer(
                packages = packages,
                maxCapacityKg = -1.0
            )
        }
    }

    @Test
    fun `should reject negative volume capacity`() {
        assertThrows(InvalidCargoInputException::class.java) {
            knapsackCargoOptimizer.optimize2D(
                packages = listOf(packageF, packageE),
                maxCapacityKg = 10.0,
                maxVolumeM3 = -1.0
            )
        }
    }

    @Test
    fun `should reject packages without volume in two dimensional optimization`() {
        assertThrows(InvalidCargoInputException::class.java) {
            knapsackCargoOptimizer.optimize2D(
                packages = packages,
                maxCapacityKg = 10.0,
                maxVolumeM3 = 8.0
            )
        }
    }
}
