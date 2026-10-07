package edu.logiroute.logiroute

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.components.PackagePriorityBadge
import edu.logiroute.logiroute.ui.components.WarehouseSummaryCard
import edu.logiroute.logiroute.ui.data.activeWarehouse
import edu.logiroute.logiroute.ui.data.emptyWarehouse
import edu.logiroute.logiroute.ui.data.lowPriorityPackage
import edu.logiroute.logiroute.ui.data.standardPriorityPackage
import edu.logiroute.logiroute.ui.data.urgentPriorityPackage

@Composable
fun App() {
    MaterialTheme {
        val packages = listOf(
            urgentPriorityPackage,
            standardPriorityPackage,
            lowPriorityPackage
        )


        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            packages.forEach { packageItem ->
                PackagePriorityBadge(
                    packageItem = packageItem,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            WarehouseSummaryCard(
                warehouse = activeWarehouse,
                modifier = Modifier.padding(bottom = 16.dp) ,

            )

            WarehouseSummaryCard(
                warehouse = emptyWarehouse
            )
        }
    }
}
