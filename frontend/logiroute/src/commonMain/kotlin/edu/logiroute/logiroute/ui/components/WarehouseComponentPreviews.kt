package edu.logiroute.logiroute.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import edu.logiroute.logiroute.ui.data.*


@Preview(name = "Urgent Priority Package")
@Composable
fun PackagePriorityBadgeUrgentPreview() {
    PackagePriorityBadge(urgentPriorityPackage)

}

@Preview(name = "Standard Priority Package")
@Composable
fun PackagePriorityBadgeStandardPreview() {

    PackagePriorityBadge(standardPriorityPackage)
}

@Preview(name = "Low Priority Package")
@Composable
fun PackagePriorityBadgeLowPreview() {

    PackagePriorityBadge(lowPriorityPackage)
}


@Preview("Empty Warehouse Summary Card")
@Composable
fun WarehouseSummaryCardEmptyPreview() {
    WarehouseSummaryCard(emptyWarehouse)

}

@Preview(name = "Active Warehouse Summary Card")
@Composable
fun WarehouseSummaryCardActivePreview() {
    WarehouseSummaryCard(activeWarehouse)
}
