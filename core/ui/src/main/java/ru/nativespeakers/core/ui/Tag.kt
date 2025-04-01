package ru.nativespeakers.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.nativespeakers.core.designsystem.Primary1
import ru.nativespeakers.core.designsystem.Primary4
import ru.nativespeakers.core.model.TagCategoryNetwork
import ru.nativespeakers.core.model.TagNetwork

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagGroup(
    group: TagCategoryNetwork,
    tags: List<TagNetwork>,
    isTagSelected: (Long) -> Boolean,
    onTagClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = group.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (tag in tags) {
                FilterChip(
                    selected = isTagSelected(tag.id),
                    onClick = { onTagClick(tag.id) },
                    label = {
                        Text(
                            text = tag.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    shape = CircleShape,
                    border = null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedLabelColor = Primary1,
                        labelColor = Primary4,
                        containerColor = Primary1,
                        selectedContainerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}