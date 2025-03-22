package ru.nativespeakers.core.ui.candidate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.nativespeakers.core.designsystem.Base5
import ru.nativespeakers.core.designsystem.Primary10
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.designsystem.TJobTheme
import ru.nativespeakers.core.ui.R
import ru.nativespeakers.core.ui.interview.PersonAndPhotoUiState
import ru.nativespeakers.core.ui.photo.PersonPhoto

@Immutable
data class CandidateCardUiState(
    val id: Long = 0,
    val candidate: PersonAndPhotoUiState = PersonAndPhotoUiState(),
    val vacancyCount: Int = 0,
)

@Composable
fun CandidateCard(
    state: CandidateCardUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 4.dp,
            hoveredElevation = 4.dp,
            focusedElevation = 4.dp,
            draggedElevation = 4.dp,
            disabledElevation = 4.dp
        ),
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            PersonPhoto(
                state = state.candidate,
                modifier = Modifier.size(42.dp)
            )
            Column {
                Text(
                    text = "${state.candidate.name} ${state.candidate.surname}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Primary10
                )
                Text(
                    text = "${state.vacancyCount} ${stringResource(R.string.core_ui_vacancies_count)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Base5
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = Primary6,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Preview
@Composable
private fun CandidateCardPreview() {
    TJobTheme {
        CandidateCard(
            state = CandidateCardUiState(
                id = 0,
                candidate = PersonAndPhotoUiState(
                    name = "Пьер",
                    surname = "Безухов",
                    photoUrl = null
                ),
                vacancyCount = 7
            ),
            onClick = {},
            modifier = Modifier.width(300.dp)
        )
    }
}