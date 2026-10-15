package di

import com.google.common.truth.Truth.assertThat
import domain.service.TelemetryStreamingService
import domain.usecase.telemetry.TrackActiveFleetUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

class TelemetryModuleTest : KoinTest {

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(telemetryModule)
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should resolve TelemetryStreamingService from telemetryModule`() {
        val service: TelemetryStreamingService = get()
        assertThat(service).isNotNull()
    }

    @Test
    fun `should resolve TrackActiveFleetUseCase from telemetryModule`() {
        val useCase: TrackActiveFleetUseCase = get()
        assertThat(useCase).isNotNull()
    }
}
