
package domain.usecase.analytics

import com.google.common.truth.Truth.assertThat
import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.Warehouse
import org.junit.jupiter.api.Test

class ClassifyPackageSectorUseCaseTest {

    private val useCase = ClassifyPackageSectorUseCase()

    private fun createHub(
        id: String,
        name: String,
        sector: RegionalZone,
        latitude: Double,
        longitude: Double
    ): Warehouse = Warehouse(
        id = id,
        name = name,
        regionalZone = sector,
        latitude = latitude,
        longitude = longitude
    )

    private fun createPackage(
        id: String,
        destinationHub: Warehouse,
        weight: Double = DEFAULT_WEIGHT
    ): Package {
        val originHub = createHub(
            id = ORIGIN_HUB_ID,
            name = ORIGIN_HUB_NAME,
            sector = RegionalZone.CENTRAL,
            latitude = ORIGIN_LATITUDE,
            longitude = ORIGIN_LONGITUDE
        )

        return Package(
            id = id,
            weight = weight,
            priority = Priority.STANDARD,
            originHub = originHub,
            destinationHub = destinationHub
        )
    }

    private fun createTrainingData(): List<Package> =
        listOf(
            createPackage(
                id = "PKG-000001",
                destinationHub = createHub(
                    id = "WH-001",
                    name = "Hub-001",
                    sector = RegionalZone.NORTH,
                    latitude = 32.01,
                    longitude = -116.21
                )
            ),
            createPackage(
                id = "PKG-000002",
                destinationHub = createHub(
                    id = "WH-002",
                    name = "Hub-002",
                    sector = RegionalZone.NORTH,
                    latitude = 32.02,
                    longitude = -116.21
                )
            ),
            createPackage(
                id = "PKG-000003",
                destinationHub = createHub(
                    id = "WH-004",
                    name = "Hub-004",
                    sector = RegionalZone.NORTH,
                    latitude = 32.03,
                    longitude = -116.21
                )
            ),
            createPackage(
                id = "PKG-000004",
                destinationHub = createHub(
                    id = "WH-005",
                    name = "Hub-005",
                    sector = RegionalZone.SOUTH,
                    latitude = 32.04,
                    longitude = -116.21
                )
            ),
            createPackage(
                id = "PKG-000005",
                destinationHub = createHub(
                    id = "WH-006",
                    name = "Hub-006",
                    sector = RegionalZone.SOUTH,
                    latitude = 32.05,
                    longitude = -116.21
                )
            )
        )

    @Test
    fun `classifies package using majority vote of five nearest neighbors`() {
        val result = useCase(
            latitude = QUERY_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = DEFAULT_WEIGHT,
            historicalPackages = createTrainingData()
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow())
            .isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `uses only the five nearest historical packages`() {
        val historicalPackages = createTrainingData() +
                createPackage(
                    id = "PKG-000006",
                    destinationHub = createHub(
                        id = "WH-007",
                        name = "Hub-007",
                        sector = RegionalZone.WEST,
                        latitude = DISTANT_LATITUDE,
                        longitude = QUERY_LONGITUDE
                    )
                )

        val result = useCase(
            latitude = QUERY_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = DEFAULT_WEIGHT,
            historicalPackages = historicalPackages
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow())
            .isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `breaks a voting tie using the closest neighbor from tied sectors`() {
        val historicalPackages = listOf(
            createPackage(
                id = "PKG-000001",destinationHub = createHub(id = "WH-001", name = "Hub-001",
                    sector = RegionalZone.NORTH,

                    latitude = 32.01,
                    longitude = QUERY_LONGITUDE
                )
            ),
            createPackage(
                id = "PKG-000002", destinationHub = createHub(id = "WH-002", name = "Hub-002",
                    sector = RegionalZone.SOUTH, latitude = 32.02,
                    longitude = QUERY_LONGITUDE
                )
            ),
            createPackage(
                id = "PKG-000003",
                destinationHub = createHub(id = "WH-004", name = "Hub-004", sector = RegionalZone.NORTH,
                    latitude = 32.03,
                    longitude = QUERY_LONGITUDE
                )
            ),
            createPackage(
                id = "PKG-000004",
                destinationHub = createHub(id = "WH-005", name = "Hub-005",
                    sector = RegionalZone.SOUTH,
                    latitude = 32.04,
                    longitude = QUERY_LONGITUDE
                )
            ),
            createPackage(
                id = "PKG-000005",
                destinationHub = createHub(id = "WH-006", name = "Hub-006",
                    sector = RegionalZone.WEST,
                    latitude = 32.05,
                    longitude = QUERY_LONGITUDE
                )
            )
        )

        val result = useCase(
            latitude = QUERY_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = DEFAULT_WEIGHT,
            historicalPackages = historicalPackages
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow())
            .isEqualTo(RegionalZone.NORTH)
    }

    @Test
    fun `returns failure when latitude is outside valid range`() {
        val result = useCase(
            latitude = INVALID_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = DEFAULT_WEIGHT,
            historicalPackages = createTrainingData()
        )

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `returns failure when longitude is outside valid range`() {
        val result = useCase(
            latitude = QUERY_LATITUDE,
            longitude = INVALID_LONGITUDE,
            weight = DEFAULT_WEIGHT,
            historicalPackages = createTrainingData()
        )

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `returns failure when weight is zero`() {
        val result = useCase(
            latitude = QUERY_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = MIN_WEIGHT,
            historicalPackages = createTrainingData()
        )

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `returns failure when weight is negative`() {
        val result = useCase(
            latitude = QUERY_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = NEGATIVE_WEIGHT,
            historicalPackages = createTrainingData()
        )

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `returns failure when fewer than five historical packages exist`() {
        val result = useCase(
            latitude = QUERY_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = DEFAULT_WEIGHT,
            historicalPackages = createTrainingData().take(INSUFFICIENT_COUNT)
        )

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `returns failure when latitude is NaN`() {
        val result = useCase(
            latitude = Double.NaN,
            longitude = QUERY_LONGITUDE,
            weight = DEFAULT_WEIGHT,
            historicalPackages = createTrainingData()
        )

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `returns failure when weight is infinite`() {
        val result = useCase(
            latitude = QUERY_LATITUDE,
            longitude = QUERY_LONGITUDE,
            weight = Double.POSITIVE_INFINITY,
            historicalPackages = createTrainingData()
        )

        assertThat(result.isFailure).isTrue()
    }

    private companion object {
        const val DEFAULT_WEIGHT = 134.37
        const val MIN_WEIGHT = 0.0
        const val NEGATIVE_WEIGHT = -10.0

        const val QUERY_LATITUDE = 32.0
        const val QUERY_LONGITUDE = -116.21
        const val DISTANT_LATITUDE = 60.0

        const val INVALID_LATITUDE = 91.0
        const val INVALID_LONGITUDE = 181.0

        const val INSUFFICIENT_COUNT = 4

        const val ORIGIN_HUB_ID = "WH-098"
        const val ORIGIN_HUB_NAME = "Hub-098"
        const val ORIGIN_LATITUDE = 37.91
        const val ORIGIN_LONGITUDE = -88.46
    }
}
