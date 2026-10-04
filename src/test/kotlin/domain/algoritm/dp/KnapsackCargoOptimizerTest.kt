package domain.algoritm.dp

import com.google.common.truth.Truth.assertThat
import domain.model.Package
import domain.model.Priority
import domain.model.Warehouse
import io.mockk.mockk
import org.junit.jupiter.api.Test

class KnapsackCargoOptimizerTest {

    private val origin = mockk<Warehouse>()
    private val destination = mockk<Warehouse>()
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

    private val packages = listOf(
        packageA,
        packageB,
        packageC,
        packageD
    )

    @Test
    fun `should select package combination with maximum priority without exceeding capacity`() {
        val optimizer = KnapsackCargoOptimizer()

        val result = optimizer.optimize(
            packages = packages,
            maxCapacityKg = 10
        )

        assertThat(result).containsExactly(packageA,packageC)
    }
}
