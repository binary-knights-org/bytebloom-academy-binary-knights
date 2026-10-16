package di

import domain.service.TelemetryStreamingService
import org.koin.dsl.module

val telemetryModule = module {
    single { TelemetryStreamingService(vehicleRepository = getOrNull()) }
}
