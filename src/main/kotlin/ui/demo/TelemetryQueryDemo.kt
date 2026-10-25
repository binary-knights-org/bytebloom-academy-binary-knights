package ui.demo

import domain.service.TelemetryQueryService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

suspend fun runTelemetryQueryDemo() {
    val stats = SearchStats()
    val service = createService(stats)

    val rapidSearches = runRapidInputScenario(service, stats)
    val duplicateSearches = runDuplicateScenario(service, stats)
    val cancellation = runCancellationScenario(service, stats)

    printSummary(stats, rapidSearches, duplicateSearches, cancellation)
}

private fun createService(stats: SearchStats) = TelemetryQueryService { query ->
    val id = stats.searches.incrementAndGet()

    println("[SEARCH STARTED] Query: '$query'")
    println("[NETWORK/DB] Executing search #$id")

    try {
        delay(SEARCH_LATENCY_MS)

        println("[SEARCH COMPLETED] Query: '$query'")
        listOf("Vehicle result for $query")
    } catch (e: CancellationException) {
        stats.cancellations.incrementAndGet()
        println("[SEARCH CANCELLED] Query: '$query'")
        throw e
    }
}

// Five keystrokes within 200 ms -> one search for TRK-101
private suspend fun runRapidInputScenario(
    service: TelemetryQueryService,
    stats: SearchStats
): Int {
    println("\n========== SCENARIO 1: RAPID INPUT ==========")

    val searchesBefore = stats.searches.get()

    val rapidInput = flow {
        listOf("T", "TR", "TRK", "TRK-", "TRK-101").forEach { query ->
            println("[INPUT] User typed: '$query'")
            emit(query)
            delay(KEYSTROKE_INTERVAL_MS)
        }
    }

    service.queryStream(rapidInput).collect { results ->
        println("[RESULT] $results")
    }

    val rapidSearches = stats.searches.get() - searchesBefore

    println("\n[VERIFY] Expected searches: 1")
    println("[VERIFY] Actual searches: $rapidSearches")
    println("[VERIFY] Passed: ${rapidSearches == 1}")

    return rapidSearches
}

// Consecutive duplicate queries -> TRK-201 and TRK-202 only
private suspend fun runDuplicateScenario(
    service: TelemetryQueryService,
    stats: SearchStats
): Int {
    println("\n========== SCENARIO 2: DUPLICATE QUERY FILTERING ==========")

    val searchesBefore = stats.searches.get()

    val duplicateInput = flow {
        println("[INPUT] User typed: 'TRK-201'")
        emit("TRK-201")
        delay(SEARCH_COMPLETION_WAIT_MS)

        println("[INPUT] User typed: 'TRK-201' again")
        emit("TRK-201")
        delay(PAST_DEBOUNCE_MS)

        println("[INPUT] User typed: 'TRK-202'")
        emit("TRK-202")
        delay(SEARCH_COMPLETION_WAIT_MS)
    }

    service.queryStream(duplicateInput).collect { results ->
        println("[RESULT] $results")
    }

    val duplicateSearches = stats.searches.get() - searchesBefore

    println("\n[VERIFY] Expected searches: 2")
    println("[VERIFY] Actual searches: $duplicateSearches")
    println("[VERIFY] Passed: ${duplicateSearches == 2}")

    return duplicateSearches
}

// In-flight search cancelled -> TRK-301 cancelled, TRK-302 completes
private suspend fun runCancellationScenario(
    service: TelemetryQueryService,
    stats: SearchStats
): CancellationResult {
    println("\n========== SCENARIO 3: CANCELLATION ==========")

    val searchesBefore = stats.searches.get()
    val cancellationsBefore = stats.cancellations.get()

    val changingInput = flow {
        println("[INPUT] User typed: 'TRK-301'")
        emit("TRK-301")
        delay(PAST_DEBOUNCE_MS)

        println("[INPUT] User typed: 'TRK-302'")
        emit("TRK-302")
        delay(SEARCH_COMPLETION_WAIT_MS)
    }

    service.queryStream(changingInput).collect { results ->
        println("[RESULT] $results")
    }

    val searches = stats.searches.get() - searchesBefore
    val cancellations = stats.cancellations.get() - cancellationsBefore

    println("\n[VERIFY] Expected searches started: 2")
    println("[VERIFY] Actual searches started: $searches")
    println("[VERIFY] Search count passed: ${searches == 2}")

    println("[VERIFY] Expected cancellations: 1")
    println("[VERIFY] Actual cancellations: $cancellations")
    println("[VERIFY] Cancellation passed: ${cancellations == 1}")

    return CancellationResult(searches, cancellations)
}

private fun printSummary(
    stats: SearchStats,
    rapidSearches: Int,
    duplicateSearches: Int,
    cancellation: CancellationResult
) {
    val allPassed =
        rapidSearches == 1 &&
                duplicateSearches == 2 &&
                cancellation.searches == 2 &&
                cancellation.cancellations == 1

    println("\n========== FINAL SUMMARY ==========")
    println("[SUMMARY] Total search executions: ${stats.searches.get()}")
    println("[SUMMARY] Total cancelled searches: ${stats.cancellations.get()}")
    println("[SUMMARY] Rapid-input test: ${rapidSearches == 1}")
    println("[SUMMARY] Duplicate-filter test: ${duplicateSearches == 2}")
    println("[SUMMARY] Cancellation test: ${cancellation.cancellations == 1}")
    println("[SUMMARY] ALL TESTS PASSED: $allPassed")
}

private const val SEARCH_LATENCY_MS = 1_000L
private const val KEYSTROKE_INTERVAL_MS = 30L
private const val PAST_DEBOUNCE_MS = 350L
private const val SEARCH_COMPLETION_WAIT_MS = 1_400L

