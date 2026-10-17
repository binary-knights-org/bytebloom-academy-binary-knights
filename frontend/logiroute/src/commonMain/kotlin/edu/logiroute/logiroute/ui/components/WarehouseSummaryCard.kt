package edu.logiroute.logiroute.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.Priority
import domain.model.Warehouse
import edu.logiroute.logiroute.ui.theme.Border
import edu.logiroute.logiroute.ui.theme.CharcoalBlue
import edu.logiroute.logiroute.ui.theme.TextDisabled
import edu.logiroute.logiroute.ui.theme.TextPrimary

@Composable
fun WarehouseSummaryCard(
    warehouse: Warehouse,
    modifier: Modifier = Modifier
) {
    val highestPriorityPackage = warehouse.cargoQueue.maxByOrNull { it.priority.rank() }

    Surface(
        modifier = modifier.border(
            width = 1.dp,
            color = Border,
            shape = MaterialTheme.shapes.medium
        ),
        color = CharcoalBlue,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            WarehouseIdentityBadge(
                warehouse = warehouse,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Queued Packages: ${warehouse.cargoQueue.size}",
                    color = TextPrimary
                )

                Text(
                    text = "Stationed Vehicles: ${warehouse.stationedVehicles.size}",
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (highestPriorityPackage != null) {
                PackagePriorityBadge(
                    packageItem = highestPriorityPackage,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "No Pending Cargo",
                    color = TextDisabled
                )
            }
        }
    }
}

private fun Priority.rank(): Int =
    when (this) {
        Priority.URGENT -> 2
        Priority.STANDARD -> 1
        else -> 0

    }

