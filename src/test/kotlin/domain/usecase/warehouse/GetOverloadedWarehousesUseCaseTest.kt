package domain.usecase.warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class GetOverloadedWarehousesUseCaseTest {

    private val getWarehouseLoadFactorUseCase =
        mockk<GetWarehouseLoadFactorUseCase>()

    private val warehouseRepository =
        mockk<WarehouseRepository>()

    private val useCase = GetOverloadedWarehousesUseCase(
        getWarehouseLoadFactorUseCase = getWarehouseLoadFactorUseCase,
        warehouseRepository = warehouseRepository
    )

    @Test
    fun `should return empty list when there are no warehouses`() = runTest {
        // Given
        coEvery {
            warehouseRepository.getAll()
        } returns emptyList()

        // When
        val result = useCase()

        // Then
        assertEquals(emptyList(), result)

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }
    }

    @Test
    fun `should return warehouse when load factor is greater than threshold`() = runTest {
        // Given
        val warehouse = mockk<Warehouse>()
        val warehouseId = "WH-001"

        every {
            warehouse.id
        } returns warehouseId

        coEvery {
            warehouseRepository.getAll()
        } returns listOf(warehouse)

        coEvery {
            getWarehouseLoadFactorUseCase(warehouseId)
        } returns 1.5

        // When
        val result = useCase()

        // Then
        assertEquals(listOf(warehouse), result)

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }

        coVerify(exactly = 1) {
            getWarehouseLoadFactorUseCase(warehouseId)
        }
    }

    @Test
    fun `should exclude warehouse when load factor is equal to threshold`() = runTest {
        // Given
        val warehouse = mockk<Warehouse>()
        val warehouseId = "WH-001"

        every {
            warehouse.id
        } returns warehouseId

        coEvery {
            warehouseRepository.getAll()
        } returns listOf(warehouse)

        coEvery {
            getWarehouseLoadFactorUseCase(warehouseId)
        } returns 1.0

        // When
        val result = useCase()

        // Then
        assertEquals(emptyList(), result)

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }

        coVerify(exactly = 1) {
            getWarehouseLoadFactorUseCase(warehouseId)
        }
    }

    @Test
    fun `should exclude warehouse when load factor is below threshold`() = runTest {
        // Given
        val warehouse = mockk<Warehouse>()
        val warehouseId = "WH-001"

        every {
            warehouse.id
        } returns warehouseId

        coEvery {
            warehouseRepository.getAll()
        } returns listOf(warehouse)

        coEvery {
            getWarehouseLoadFactorUseCase(warehouseId)
        } returns 0.75

        // When
        val result = useCase()

        // Then
        assertEquals(emptyList(), result)

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }

        coVerify(exactly = 1) {
            getWarehouseLoadFactorUseCase(warehouseId)
        }
    }

    @Test
    fun `should return only overloaded warehouses`() = runTest {
        // Given
        val overloadedWarehouse = mockk<Warehouse>()
        val normalWarehouse = mockk<Warehouse>()
        val boundaryWarehouse = mockk<Warehouse>()

        every {
            overloadedWarehouse.id
        } returns "WH-001"

        every {
            normalWarehouse.id
        } returns "WH-002"

        every {
            boundaryWarehouse.id
        } returns "WH-003"

        coEvery {
            warehouseRepository.getAll()
        } returns listOf(
            overloadedWarehouse,
            normalWarehouse,
            boundaryWarehouse
        )

        coEvery {
            getWarehouseLoadFactorUseCase("WH-001")
        } returns 1.25

        coEvery {
            getWarehouseLoadFactorUseCase("WH-002")
        } returns 0.80

        coEvery {
            getWarehouseLoadFactorUseCase("WH-003")
        } returns 1.0

        // When
        val result = useCase()

        // Then
        assertEquals(
            listOf(overloadedWarehouse),
            result
        )

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }

        coVerify(exactly = 1) {
            getWarehouseLoadFactorUseCase("WH-001")
            getWarehouseLoadFactorUseCase("WH-002")
            getWarehouseLoadFactorUseCase("WH-003")
        }
    }

    @Test
    fun `should propagate repository exception`() = runTest {
        // Given
        val repositoryException = IllegalStateException(
            "Failed to load warehouses"
        )

        coEvery {
            warehouseRepository.getAll()
        } throws repositoryException

        // When
        val exception = assertFailsWith<IllegalStateException> {
            useCase()
        }

        // Then
        assertEquals(repositoryException, exception)

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }
    }

    @Test
    fun `should propagate load factor exception`() = runTest {
        // Given
        val warehouse = mockk<Warehouse>()
        val warehouseId = "WH-001"
        val loadFactorException = IllegalStateException(
            "Failed to calculate load factor"
        )

        every {
            warehouse.id
        } returns warehouseId

        coEvery {
            warehouseRepository.getAll()
        } returns listOf(warehouse)

        coEvery {
            getWarehouseLoadFactorUseCase(warehouseId)
        } throws loadFactorException

        // When
        val exception = assertFailsWith<IllegalStateException> {
            useCase()
        }

        // Then
        assertEquals(loadFactorException, exception)

        coVerify(exactly = 1) {
            warehouseRepository.getAll()
        }

        coVerify(exactly = 1) {
            getWarehouseLoadFactorUseCase(warehouseId)
        }
    }
}
