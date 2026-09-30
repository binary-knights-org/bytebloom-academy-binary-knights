package domain.usecase.shipment

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import domain.repository.WarehouseRepository
import domain.model.Warehouse
import domain.model.RegionalZone
import domain.model.Package
import domain.model.Priority
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test



class CalculateAveragePackageWeightUseCaseTest {

    private val warehouseRepository = mockk<WarehouseRepository>()
    private val useCase = CalculateAveragePackageWeightUseCase(warehouseRepository)

    private val origin = Warehouse(
        name = "Origin",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    )

    private val destination = Warehouse(
        name = "Destination",
        regionalZone = RegionalZone.SOUTH,
        latitude = 1.0,
        longitude = 1.0
    )

    private val warehouse = Warehouse(
        name = "Hub",
        regionalZone = RegionalZone.NORTH,
        latitude = 0.0,
        longitude = 0.0
    ).apply {
        addPackage(Package(
            weight = 10.0,
            priority = Priority.URGENT,
            originHub = origin,
            destinationHub = destination
         )
        )
        addPackage(Package(
            weight = 20.0,
            priority = Priority.LOW,
            originHub = origin,
            destinationHub = destination
         )
        )
    }

    @Test
    fun `should calculate average package weight`() = runTest {
        // Given
        coEvery { warehouseRepository.getAll() } returns listOf(warehouse)

        // When
        val result = useCase()

        // Then
        assertThat(result).isEqualTo(15.0)
    }

    @Test
    fun `should return zero average weight when there are no packages`() = runTest {
        // Given
        coEvery { warehouseRepository.getAll() } returns emptyList()

        // When
        val result = useCase()

        // Then
        assertThat(result).isEqualTo(0.0)
    }
}