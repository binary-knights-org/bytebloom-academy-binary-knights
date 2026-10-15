package di

import domain.service.TelemetryStreamingService
import domain.usecase.telemetry.TrackActiveFleetUseCase
import org.koin.dsl.module

val telemetryModule = module {
    single { TelemetryStreamingService(vehicleRepository = getOrNull()) }
    factory { TrackActiveFleetUseCase(telemetryService = get()) }
}
