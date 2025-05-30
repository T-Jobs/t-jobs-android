package ru.nativespeakers.core.ui.interview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import ru.nativespeakers.core.designsystem.Primary10
import ru.nativespeakers.core.designsystem.Primary2
import ru.nativespeakers.core.designsystem.Primary3
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.designsystem.Primary8
import ru.nativespeakers.core.designsystem.TJobTheme
import ru.nativespeakers.core.model.InterviewNetwork
import ru.nativespeakers.core.model.InterviewStatus
import ru.nativespeakers.core.model.TrackNetwork
import ru.nativespeakers.core.ui.conditional
import ru.nativespeakers.core.ui.photo.PersonPhoto
import ru.nativespeakers.core.ui.toUi

@Immutable
data class InterviewCardUiState(
    val interviewId: Long = 0,
    val interviewName: String = "",
    val interviewerUiState: PersonAndPhotoUiState? = null,
    val candidateUiState: PersonAndPhotoUiState = PersonAndPhotoUiState(),
    val status: InterviewStatus = InterviewStatus.NONE,
    val date: LocalDateTime? = null,
)

@Immutable
data class PersonAndPhotoUiState(
    val name: String = "",
    val surname: String = "",
    val photoUrl: String? = null,
)

@Composable
fun InterviewCard(
    interviewCardUiState: InterviewCardUiState,
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
        InterviewCardHeader(
            interviewName = interviewCardUiState.interviewName,
            date = interviewCardUiState.date,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        )
        InterviewCardFooter(
            interviewerUiState = interviewCardUiState.interviewerUiState,
            candidateUiState = interviewCardUiState.candidateUiState,
            status = interviewCardUiState.status,
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun InterviewCardHeader(
    interviewName: String,
    date: LocalDateTime?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = date.toUi(),
                color = Primary6,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = interviewName,
                color = Primary10,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = Primary8,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun InterviewCardFooter(
    interviewerUiState: PersonAndPhotoUiState?,
    candidateUiState: PersonAndPhotoUiState,
    status: InterviewStatus,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        InterviewCardPerson(
            personUiState = interviewerUiState,
            isInterviewer = true,
            modifier = Modifier.size(width = 70.dp, height = 72.dp)
        )
        InterviewCardPerson(
            personUiState = candidateUiState,
            isInterviewer = false,
            modifier = Modifier.size(width = 70.dp, height = 72.dp)
        )
        InterviewStatusCard(
            status = status,
            modifier = Modifier
                .height(72.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun InterviewCardPerson(
    personUiState: PersonAndPhotoUiState?,
    isInterviewer: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .conditional(
                condition = isInterviewer,
                trueBlock = {
                    background(Primary2)
                },
                falseBlock = {
                    border(
                        width = 1.dp,
                        color = Primary3,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            )
            .padding(4.dp)
    ) {
        Spacer(Modifier.weight(1f))
        PersonPhoto(
            state = personUiState,
            modifier = Modifier.size(26.dp)
        )
        if (personUiState != null) {
            Text(
                text = "${personUiState.name} ${personUiState.surname}",
                textAlign = TextAlign.Center,
                color = Primary10,
                style = MaterialTheme.typography.labelMedium,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

fun interviewCardUiState(
    interviewer: PersonAndPhotoUiState?,
    interview: InterviewNetwork,
    track: TrackNetwork,
) = InterviewCardUiState(
    interviewId = interview.id,
    interviewName = interview.interviewType.name,
    interviewerUiState = interviewer,
    candidateUiState = PersonAndPhotoUiState(
        name = track.candidate.name,
        surname = track.candidate.surname,
        photoUrl = track.candidate.photoUrl
    ),
    status = interview.status,
    date = interview.datePicked
)

@Preview
@Composable
private fun InterviewCardPreview() {
    TJobTheme {
        InterviewCard(
            interviewCardUiState = InterviewCardUiState(
                interviewName = "Алгоритмическое интервью",
                interviewerUiState = null,
                candidateUiState = PersonAndPhotoUiState(
                    name = "Алексей",
                    surname = "Трясков",
                    photoUrl = null
                ),
                status = InterviewStatus.PASSED,
                date = LocalDateTime(
                    year = 2025,
                    monthNumber = 2,
                    dayOfMonth = 8,
                    hour = 13,
                    minute = 45,
                    second = 0,
                    nanosecond = 0
                )
            ),
            onClick = {},
            modifier = Modifier.width(400.dp)
        )
    }
}