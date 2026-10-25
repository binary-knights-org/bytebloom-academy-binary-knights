package ui.demo

import domain.service.TelemetryQueryService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger

private const val SEARCH_LATENCY_MS = 1_000L          // simulated network/database latency
private const val KEYSTROKE_INTERVAL_MS = 30L         // time between two fast keystrokes
private const val PAST_DEBOUNCE_MS = 350L             // slightly longer than the 300 ms debounce window
private const val SEARCH_COMPLETION_WAIT_MS = 1_400L  // long enough for a 1000 ms search to finish

private val startTime = System.currentTimeMillis()

private fun log(message: String) =
    println("[+${System.currentTimeMillis() - startTime}ms] $message")

private class SearchStats {
    val searches = AtomicInteger(0)
    val cancellations = AtomicInteger(0)
}

private data class CancellationResult(
    val searches: Int,
    val cancellations: Int
)

fun main() = runBlocking {
    val stats = SearchStats()
    val service = createService(stats)

    val rapidSearches = runRapidInputScenario(service, stats)
    val duplicateSearches = runDuplicateScenario(service, stats)
    val cancellation = runCancellationScenario(service, stats)

    printSummary(stats, rapidSearches, duplicateSearches, cancellation)
}

// Logging and simulated network/database work stay in the demo
private fun createService(stats: SearchStats) = TelemetryQueryService { query ->
    val id = stats.searches.incrementAndGet()

    log("[SEARCH STARTED] Query: '$query'")
    log("[NETWORK/DB] Executing search #$id")

    try {
        delay(SEARCH_LATENCY_MS)

        log("[SEARCH COMPLETED] Query: '$query'")
        listOf("Vehicle result for $query")
    } catch (e: CancellationException) {
        stats.cancellations.incrementAndGet()
        log("[SEARCH CANCELLED] Query: '$query'")
        throw e
    }
}

// =====================================================
// SCENARIO 1: Five keystrokes within 200 ms
// Expected: One search for TRK-101
// =====================================================
private suspend fun runRapidInputScenario(
    service: TelemetryQueryService,
    stats: SearchStats
): Int {
    println("\n========== SCENARIO 1: RAPID INPUT ==========")

    val searchesBefore = stats.searches.get()

    val rapidInput = flow {
        listOf("T", "TR", "TRK", "TRK-", "TRK-101").forEach { query ->
            log("[INPUT] User typed: '$query'")
            emit(query)
            delay(KEYSTROKE_INTERVAL_MS)
        }
    }

    service.queryStream(rapidInput).collect { results ->
        log("[RESULT] $results")
    }

    val rapidSearches = stats.searches.get() - searchesBefore

    println("\n[VERIFY] Expected searches: 1")
    println("[VERIFY] Actual searches: $rapidSearches")
    println("[VERIFY] Passed: ${rapidSearches == 1}")

    return rapidSearches
}

// =====================================================
// SCENARIO 2: Consecutive duplicate queries
// Expected: TRK-201 and TRK-202 only
// =====================================================
private suspend fun runDuplicateScenario(
    service: TelemetryQueryService,
    stats: SearchStats
): Int {
    println("\n========== SCENARIO 2: DUPLICATE QUERY FILTERING ==========")

    val searchesBefore = stats.searches.get()

    val duplicateInput = flow {
        log("[INPUT] User typed: 'TRK-201'")
        emit("TRK-201")

        // Let the first search complete.
        delay(SEARCH_COMPLETION_WAIT_MS)

        log("[INPUT] User typed: 'TRK-201' again")
        emit("TRK-201")

        // The repeated value must be filtered by distinctUntilChanged.
        delay(PAST_DEBOUNCE_MS)

        log("[INPUT] User typed: 'TRK-202'")
        emit("TRK-202")

        // Let the final search complete.
        delay(SEARCH_COMPLETION_WAIT_MS)
    }

    service.queryStream(duplicateInput).collect { results ->
        log("[RESULT] $results")
    }

    val duplicateSearches = stats.searches.get() - searchesBefore

    println("\n[VERIFY] Expected searches: 2")
    println("[VERIFY] Actual searches: $duplicateSearches")
    println("[VERIFY] Passed: ${duplicateSearches == 2}")

    return duplicateSearches
}

// =====================================================
// SCENARIO 3: Cancel an in-flight search
// Expected: TRK-301 cancelled; TRK-302 completes
// Cancellation happens when TRK-302 passes the debounce
// (~350 ms + 300 ms), not at the moment it is typed.
// =====================================================
private suspend fun runCancellationScenario(
    service: TelemetryQueryService,
    stats: SearchStats
): CancellationResult {
    println("\n========== SCENARIO 3: CANCELLATION ==========")

    val searchesBefore = stats.searches.get()
    val cancellationsBefore = stats.cancellations.get()

    val changingInput = flow {
        log("[INPUT] User typed: 'TRK-301'")
        emit("TRK-301")

        // The first search starts after the debounce window.
        delay(PAST_DEBOUNCE_MS)

        log("[INPUT] User typed: 'TRK-302'")
        emit("TRK-302")

        // Allow the second search to start and complete.
        delay(SEARCH_COMPLETION_WAIT_MS)
    }

    service.queryStream(changingInput).collect { results ->
        log("[RESULT] $results")
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

// =====================================================
// FINAL SUMMARY
// =====================================================
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
