package ru.nativespeakers.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.nativespeakers.core.designsystem.Base7
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.designsystem.Red5
import ru.nativespeakers.core.designsystem.TJobTheme

@Composable
fun Competency(
    text: String,
    withDraggableIcon: Boolean,
    withHelperIcon: Boolean,
    withDeleteIcon: Boolean,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp)
    ) {
        if (withDraggableIcon) {
            Icon(
                imageVector = Icons.Outlined.DragHandle,
                contentDescription = null,
                tint = Base7,
                modifier = Modifier.size(24.dp)
            )
        }

        if (withHelperIcon) {
            Icon(
                imageVector = Icons.Outlined.Construction,
                contentDescription = null,
                tint = Primary6,
                modifier = Modifier.size(17.dp)
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        if (withDeleteIcon) {
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = null,
                tint = Red5,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDeleteClick
                    )
            )
        }
    }
}

@Preview(name = "Without Draggable Icon and With Helper Icon")
@Composable
private fun CompetencyPreview1() {
    TJobTheme {
        Competency(
            text = "Алгоритмическое интервью",
            withDraggableIcon = false,
            withHelperIcon = true,
            withDeleteIcon = true,
            onDeleteClick = {}
        )
    }
}

@Preview(name = "With Draggable Icon and With Helper Icon")
@Composable
private fun CompetencyPreview2() {
    TJobTheme {
        Competency(
            text = "Алгоритмическое интервью",
            withDraggableIcon = true,
            withHelperIcon = true,
            withDeleteIcon = true,
            onDeleteClick = {}
        )
    }
}

@Preview(name = "With Draggable Icon and Without Helper Icon")
@Composable
private fun CompetencyPreview3() {
    TJobTheme {
        Competency(
            text = "Алгоритмическое интервью",
            withDraggableIcon = true,
            withHelperIcon = false,
            withDeleteIcon = true,
            onDeleteClick = {}
        )
    }
}

@Preview(name = "Without Draggable Icon and Without Helper Icon")
@Composable
private fun CompetencyPreview4() {
    TJobTheme {
        Competency(
            text = "Алгоритмическое интервью",
            withDraggableIcon = false,
            withHelperIcon = false,
            withDeleteIcon = true,
            onDeleteClick = {}
        )
    }
}