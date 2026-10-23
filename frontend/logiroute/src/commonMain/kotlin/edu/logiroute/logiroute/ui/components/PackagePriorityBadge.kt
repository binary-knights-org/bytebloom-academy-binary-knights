package edu.logiroute.logiroute.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import domain.model.Package
import domain.model.Priority
import edu.logiroute.logiroute.ui.theme.CharcoalBlue
import edu.logiroute.logiroute.ui.theme.ErrorRed
import edu.logiroute.logiroute.ui.theme.InkBlack
import edu.logiroute.logiroute.ui.theme.LightGreen
import edu.logiroute.logiroute.ui.theme.TextSecondary

@Composable
fun PackagePriorityBadge(
    packageItem: Package,
    modifier: Modifier = Modifier
) {
    val priority = packageItem.priority

    Surface(
        modifier = modifier,
        color = getPriorityBackgroundColor(priority),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${packageItem.id} - ${priority.name}",
                color = getPriorityTextColor(priority),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${packageItem.weight} kg",
                color = TextSecondary,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

private fun getPriorityBackgroundColor(priority: Priority): Color {
    return when (priority) {
        Priority.URGENT -> ErrorRed
        Priority.STANDARD -> LightGreen
        else -> CharcoalBlue
    }
}

private fun getPriorityTextColor(priority: Priority): Color {
    return when (priority) {
        Priority.URGENT, Priority.STANDARD -> InkBlack
        else -> TextSecondary
    }
}
