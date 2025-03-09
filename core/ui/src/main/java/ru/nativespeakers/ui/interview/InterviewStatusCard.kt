package ru.nativespeakers.ui.interview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.nativespeakers.core.designsystem.Blue2
import ru.nativespeakers.core.designsystem.Blue4
import ru.nativespeakers.core.designsystem.Green3
import ru.nativespeakers.core.designsystem.Green6
import ru.nativespeakers.core.designsystem.Red3
import ru.nativespeakers.core.designsystem.Red6
import ru.nativespeakers.core.designsystem.TJobTheme
import ru.nativespeakers.core.designsystem.Yellow2
import ru.nativespeakers.core.designsystem.Yellow4
import ru.nativespeakers.core.ui.R.string as coreUiStrings

enum class InterviewStatus {
    WAITING_FOR_TIME_APPROVAL,
    WAITING_FOR_FEEDBACK,
    PASSED,
    FAILED,
    NONE,
}

@Composable
fun InterviewStatusCard(
    status: InterviewStatus,
    modifier: Modifier = Modifier
) {
    when (status) {
        InterviewStatus.WAITING_FOR_TIME_APPROVAL -> WaitingForTimeApprovalCard(modifier)
        InterviewStatus.WAITING_FOR_FEEDBACK -> WaitingForFeedbackCard(modifier)
        InterviewStatus.PASSED -> InterviewPassedCard(modifier)
        InterviewStatus.FAILED -> InterviewFailedCard(modifier)
        InterviewStatus.NONE -> InterviewNoneCard(modifier)
    }
}

@Composable
private fun WaitingForTimeApprovalCard(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .border(width = 1.dp, color = Yellow4, shape = MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(Yellow2)
            .padding(6.dp)
    ) {
        Text(
            text = stringResource(coreUiStrings.core_ui_waiting_for_date_approve),
            textAlign = TextAlign.Center,
            color = Yellow4,
            style = MaterialTheme.typography.labelMedium.copy(fontStyle = FontStyle.Italic),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Icon(
            imageVector = Icons.Outlined.Alarm,
            contentDescription = null,
            tint = Yellow4,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopEnd),
        )
    }
}

@Composable
private fun WaitingForFeedbackCard(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .border(width = 1.dp, color = Blue4, shape = MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(Blue2)
            .padding(6.dp)
    ) {
        Text(
            text = stringResource(coreUiStrings.core_ui_waiting_for_feedback),
            textAlign = TextAlign.Center,
            color = Blue4,
            style = MaterialTheme.typography.labelMedium.copy(fontStyle = FontStyle.Italic),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun InterviewPassedCard(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .border(width = 1.dp, color = Green6, shape = MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(Green3)
            .padding(6.dp)
    ) {
        Text(
            text = stringResource(coreUiStrings.core_ui_interview_passed),
            textAlign = TextAlign.Center,
            color = Green6,
            style = MaterialTheme.typography.labelMedium.copy(fontStyle = FontStyle.Italic),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = Green6,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopEnd),
        )
    }
}

@Composable
private fun InterviewFailedCard(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .border(width = 1.dp, color = Red6, shape = MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(Red3)
            .padding(6.dp)
    ) {
        Text(
            text = stringResource(coreUiStrings.core_ui_interview_failed),
            textAlign = TextAlign.Center,
            color = Red6,
            style = MaterialTheme.typography.labelMedium.copy(fontStyle = FontStyle.Italic),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Icon(
            imageVector = Icons.Outlined.Cancel,
            contentDescription = null,
            tint = Red6,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopEnd),
        )
    }
}

@Composable
private fun InterviewNoneCard(modifier: Modifier = Modifier) {
    Spacer(modifier)
}

@Preview
@Composable
private fun WaitingForTimeApprovalCardPreview() {
    TJobTheme {
        WaitingForTimeApprovalCard(
            modifier = Modifier.size(width = 200.dp, height = 72.dp)
        )
    }
}

@Preview
@Composable
private fun WaitingForFeedbackCardPreview() {
    TJobTheme {
        WaitingForFeedbackCard(
            modifier = Modifier.size(width = 200.dp, height = 72.dp)
        )
    }
}

@Preview
@Composable
private fun InterviewPassedCardPreview() {
    TJobTheme {
        InterviewPassedCard(
            modifier = Modifier.size(width = 200.dp, height = 72.dp)
        )
    }
}

@Preview
@Composable
private fun InterviewFailedCardPreview() {
    TJobTheme {
        InterviewFailedCard(
            modifier = Modifier.size(width = 200.dp, height = 72.dp)
        )
    }
}