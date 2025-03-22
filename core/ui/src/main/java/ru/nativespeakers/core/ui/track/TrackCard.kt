package ru.nativespeakers.core.ui.track

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.nativespeakers.core.designsystem.Base0
import ru.nativespeakers.core.designsystem.Base4
import ru.nativespeakers.core.designsystem.Blue4
import ru.nativespeakers.core.designsystem.Green4
import ru.nativespeakers.core.designsystem.Primary4
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.designsystem.Red4
import ru.nativespeakers.core.designsystem.TJobTheme
import ru.nativespeakers.core.designsystem.Yellow4
import ru.nativespeakers.core.model.InterviewStatus
import ru.nativespeakers.core.ui.interview.PersonAndPhotoUiState
import ru.nativespeakers.core.ui.photo.PersonPhoto
import ru.nativespeakers.core.ui.R.string as coreUiStrings

@Immutable
data class TrackCardUiState(
    val id: Long,
    val hr: PersonAndPhotoUiState,
    val candidate: PersonAndPhotoUiState,
    val interviewsCount: Int,
    val lastInterviewStatus: InterviewStatus,
    val vacancy: String,
)

@Composable
fun TrackCard(
    state: TrackCardUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 2.dp,
            hoveredElevation = 2.dp,
            focusedElevation = 2.dp,
            draggedElevation = 2.dp,
            disabledElevation = 2.dp
        ),
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Header(
            hr = state.hr,
            candidate = state.candidate,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
        )
        Footer(
            vacancy = state.vacancy,
            interviewsCount = state.interviewsCount,
            lastInterviewStatus = state.lastInterviewStatus,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
        )
    }
}

@Composable
private fun Header(
    hr: PersonAndPhotoUiState,
    candidate: PersonAndPhotoUiState,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        PersonsPhotosOnTop(
            firstTwoPersons = listOf(hr, candidate),
            photoSize = 26.dp
        )
        PersonFullName(name = "${hr.name} ${hr.surname}")
        Text(
            text = "&",
            color = Primary6,
            style = MaterialTheme.typography.labelMedium,
        )
        PersonFullName(name = "${candidate.name} ${candidate.surname}")
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = Primary6,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
private fun Footer(
    vacancy: String,
    interviewsCount: Int,
    lastInterviewStatus: InterviewStatus,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = vacancy,
            color = Base4,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$interviewsCount ${stringResource(coreUiStrings.core_ui_meetings)}",
            color = Primary4,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 8.sp)
        )
        Spacer(modifier = Modifier.width(6.dp))

        val statusColor = getStatusColor(lastInterviewStatus)
        if (statusColor != null) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(8.dp)
                    .background(color = statusColor, shape = CircleShape)
            )
        }
    }
}

@Composable
private fun PersonFullName(
    name: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .border(width = 1.dp, color = Base4, shape = MaterialTheme.shapes.medium)
            .padding(4.dp)
    ) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun PersonsPhotosOnTop(
    firstTwoPersons: List<PersonAndPhotoUiState?>,
    photoSize: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy((-12).dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        PersonPhoto(
            state = firstTwoPersons[0],
            modifier = Modifier.size(photoSize)
        )
        PersonPhoto(
            state = firstTwoPersons[1],
            modifier = Modifier
                .border(width = 1.dp, color = Base0, shape = CircleShape)
                .size(photoSize + 2.dp)
        )
    }
}

private fun getStatusColor(interviewStatus: InterviewStatus) = when (interviewStatus) {
    InterviewStatus.NONE -> null
    InterviewStatus.FAILED -> Red4
    InterviewStatus.PASSED -> Green4
    InterviewStatus.WAITING_FOR_FEEDBACK -> Blue4
    InterviewStatus.WAITING_FOR_TIME_APPROVAL -> Yellow4
}

@Preview
@Composable
private fun TrackCardPreview() {
    TJobTheme {
        TrackCard(
            state = TrackCardUiState(
                id = 0,
                hr = PersonAndPhotoUiState(
                    name = "Анна",
                    surname = "Коренина",
                    photoUrl = null
                ),
                candidate = PersonAndPhotoUiState(
                    name = "Татьяна",
                    surname = "Ларина",
                    photoUrl = null
                ),
                interviewsCount = 7,
                lastInterviewStatus = InterviewStatus.FAILED,
                vacancy = "Java-разработчик"
            ),
            onClick = {}
        )
    }
}