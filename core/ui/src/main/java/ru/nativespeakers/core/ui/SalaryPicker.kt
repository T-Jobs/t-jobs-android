package ru.nativespeakers.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.nativespeakers.core.designsystem.Primary2
import ru.nativespeakers.core.designsystem.Primary8
import ru.nativespeakers.core.ui.vacancy.moneyToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalaryPicker(
    title: String,
    state: () -> SliderState,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Slider(
            state = state(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                activeTickColor = Color.Transparent,
                inactiveTrackColor = Primary2,
                inactiveTickColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = state().valueRange.start.toInt().moneyToString(),
                color = Primary8,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = state().valueRange.endInclusive.toInt().moneyToString(),
                color = Primary8,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}