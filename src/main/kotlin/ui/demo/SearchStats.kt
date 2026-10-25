package ui.demo

import java.util.concurrent.atomic.AtomicInteger

internal class SearchStats {
    val searches = AtomicInteger(0)
    val cancellations = AtomicInteger(0)
}
