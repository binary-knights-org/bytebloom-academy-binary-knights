
package domain.usecase.analytics

import com.google.common.truth.Truth.assertThat
import domain.algorithm.knn.KNearestNeighbors
import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.InsufficientHistoricalPackagesException
import domain.model.exception.InvalidKnnWeightException
import domain.model.exception.InvalidLatitudeException
import domain.model.exception.InvalidLongitudeException
import org.junit.jupiter.api.Test

class ClassifyPackageSectorUseCaseTest {

    private val useCase = ClassifyPackageSectorUseCase(
        knn = KNearestNeighbors()
    )

    @Test
    fun `should classify sector using majority vote`() {
        val warehouses = createWarehouses()
        val packages = createHistoricalPackages(warehouses)

        val result = useCase(
            latitude = QUERY_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = QUERY_WEIGHT,
            historicalPackages = packages
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow())
            .isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `should fail when fewer than five historical packages exist`() {
        val warehouses = createWarehouses()
        val packages = createHistoricalPackages(warehouses)
            .take(FOUR_PACKAGES)

        val result = classify(packages)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull())
            .isInstanceOf(
                InsufficientHistoricalPackagesException::class.java
            )
    }

    @Test
    fun `should fail when latitude is outside valid range`() {
        val result = useCase(
            latitude = INVALID_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = QUERY_WEIGHT,
            historicalPackages = createHistoricalPackages(
                createWarehouses()
            )
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull())
            .isInstanceOf(InvalidLatitudeException::class.java)
    }

    @Test
    fun `should fail when longitude is outside valid range`() {
        val result = useCase(
            latitude = QUERY_LATITUDE,
            longitude = INVALID_LONGITUDE,
            weight = QUERY_WEIGHT,
            historicalPackages = createHistoricalPackages(
                createWarehouses()
            )
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull())
            .isInstanceOf(InvalidLongitudeException::class.java)
    }

    @Test
    fun `should fail when weight is zero`() {
        val result = useCase(
            latitude = QUERY_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = INVALID_WEIGHT,
            historicalPackages = createHistoricalPackages(
                createWarehouses()
            )
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull())
            .isInstanceOf(InvalidKnnWeightException::class.java)
    }

    @Test
    fun `should fail when latitude is NaN`() {
        val result = useCase(
            latitude = Double.NaN,
            longitude = QUERY_LONGITUDE,
            weight = QUERY_WEIGHT,
            historicalPackages = createHistoricalPackages(
                createWarehouses()
            )
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull())
            .isInstanceOf(InvalidLatitudeException::class.java)
    }

    @Test
    fun `should fail when weight is infinite`() {
        val result = useCase(
            latitude = QUERY_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = Double.POSITIVE_INFINITY,
            historicalPackages = createHistoricalPackages(
                createWarehouses()
            )
        )

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull())
            .isInstanceOf(InvalidKnnWeightException::class.java)
    }

    private fun classify(
        packages: List<Package>
    ): Result<RegionalZone> {
        return useCase(
            latitude = QUERY_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = QUERY_WEIGHT,
            historicalPackages = packages
        )
    }

    private fun createWarehouses(): Map<String, Warehouse> {
        return (
                createFirstWarehouses() +
                        createSecondWarehouses() +
                        createThirdWarehouses()
                ).associateBy { it.id }
    }

    private fun createFirstWarehouses(): List<Warehouse> {
        return listOf(
            Warehouse(
                id = "WH-001",
                name = "Hub-001",
                regionalZone = RegionalZone.CENTRAL,
                latitude = 37.91,
                longitude = -88.46
            ),
            Warehouse(
                id = "WH-002",
                name = "Hub-002",
                regionalZone = RegionalZone.WEST,
                latitude = 29.64,
                longitude = -99.44
            ),
            Warehouse(
                id = "WH-004",
                name = "Hub-004",
                regionalZone = RegionalZone.CENTRAL,
                latitude = 32.07,
                longitude = -116.21
            ),
            Warehouse(
                id = "WH-005",
                name = "Hub-005",
                regionalZone = RegionalZone.CENTRAL,
                latitude = 45.23,
                longitude = -124.97
            ),
            Warehouse(
                id = "WH-006",
                name = "Hub-006",
                regionalZone = RegionalZone.WEST,
                latitude = 31.29,
                longitude = -106.19
            )
        )
    }

    private fun createSecondWarehouses(): List<Warehouse> {
        return listOf(
            Warehouse(
                id = "WH-017",
                name = "Hub-017",
                regionalZone = RegionalZone.NORTH,
                latitude = 34.88,
                longitude = -100.00
            ),
            Warehouse(
                id = "WH-037",
                name = "Hub-037",
                regionalZone = RegionalZone.NORTH,
                latitude = 35.00,
                longitude = -101.00
            ),
            Warehouse(
                id = "WH-042",
                name = "Hub-042",
                regionalZone = RegionalZone.NORTH,
                latitude = 35.10,
                longitude = -101.10
            ),
            Warehouse(
                id = "WH-043",
                name = "Hub-043",
                regionalZone = RegionalZone.NORTH,
                latitude = 35.20,
                longitude = -101.20
            )
        )
    }

    private fun createThirdWarehouses(): List<Warehouse> {
        return listOf(
            Warehouse(
                id = "WH-072",
                name = "Hub-072",
                regionalZone = RegionalZone.CENTRAL,
                latitude = 32.10,
                longitude = -100.00
            ),
            Warehouse(
                id = "WH-073",
                name = "Hub-073",
                regionalZone = RegionalZone.CENTRAL,
                latitude = 32.20,
                longitude = -100.10
            ),
            Warehouse(
                id = "WH-095",
                name = "Hub-095",
                regionalZone = RegionalZone.SOUTH,
                latitude = 29.70,
                longitude = -99.50
            ),
            Warehouse(
                id = "WH-098",
                name = "Hub-098",
                regionalZone = RegionalZone.WEST,
                latitude = 29.80,
                longitude = -99.60
            )
        )
    }

    private fun createHistoricalPackages(
        warehouses: Map<String, Warehouse>
    ): List<Package> {
        return listOf(
            PackageFixture(
                id = "pkg-000001",
                weight = 134.37,
                originId = "WH-073",
                destinationId = "WH-037",
                priority = Priority.STANDARD
            ),
            PackageFixture(
                id = "pkg-000002",
                weight = 492.63,
                originId = " WH-098 ",
                destinationId = "WH-043",
                priority = Priority.STANDARD
            ),
            PackageFixture(
                id = "PKG-000003",
                weight = 220.10,
                originId = "WH-072",
                destinationId = "WH-042",
                priority = Priority.STANDARD
            ),
            PackageFixture(
                id = "PKG-000004",
                weight = 147.56,
                originId = "WH-095",
                destinationId = "WH-017",
                priority = Priority.LOW
            ),
            PackageFixture(
                id = "PKG-000005",
                weight = 180.00,
                originId = "WH-001",
                destinationId = "WH-037",
                priority = Priority.STANDARD
            )
        ).map { fixture ->
            createPackage(
                fixture = fixture,
                warehouses = warehouses
            )
        }
    }

    private fun createPackage(
        fixture: PackageFixture,
        warehouses: Map<String, Warehouse>
    ): Package {
        val origin = findWarehouse(
            id = fixture.originId,
            warehouses = warehouses
        )

        val destination = findWarehouse(
            id = fixture.destinationId,
            warehouses = warehouses
        )

        return Package(
            id = fixture.id.uppercase(),
            weight = fixture.weight,
            priority = fixture.priority,
            originHub = origin,
            destinationHub = destination
        )
    }

    private fun findWarehouse(
        id: String,
        warehouses: Map<String, Warehouse>
    ): Warehouse {
        return requireNotNull(
            warehouses[id.trim().uppercase()]
        ) {
            "Warehouse $id was not found."
        }
    }

    private data class PackageFixture(
        val id: String,
        val weight: Double,
        val originId: String,
        val destinationId: String,
        val priority: Priority
    )

    private companion object {
        const val QUERY_LATITUDE = 35.05
        const val QUERY_LONGITUDE = -101.05
        const val QUERY_WEIGHT = 150.0

        const val INVALID_LATITUDE = 91.0
        const val INVALID_LONGITUDE = 181.0
        const val INVALID_WEIGHT = 0.0

        const val FOUR_PACKAGES = 4
    }
}
