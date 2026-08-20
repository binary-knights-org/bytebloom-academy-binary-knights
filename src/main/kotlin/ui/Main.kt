package ui

fun main() {
    printSystemHeader()

    val repositories = initializeRepositories()
    val graph = buildDomainGraph(repositories)

    runCargoDemos(graph)
    runPricingAndDecoratorDemos(graph)
    runBreakdownSimulationDemo()
    runRoutingAndComparisonDemos(graph)

    printSystemFooter()
}

private fun printSystemHeader() {
    println(
        """
        
    ========================================================================
                                                                          
              BYTEBLOOM ACADEMY: LOGISTICS & ROUTING ENGINE        
                                                                          
    ========================================================================
    """.trimIndent()
    )
}

private fun printSystemFooter() {
    println(
        """
        
    ========================================================================
                   SYSTEM EXECUTION COMPLETED SUCCESSFULLY              
    ========================================================================
    
    """.trimIndent()
    )
}
