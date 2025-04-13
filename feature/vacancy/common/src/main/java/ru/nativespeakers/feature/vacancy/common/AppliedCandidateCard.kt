package ru.nativespeakers.feature.vacancy.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.nativespeakers.core.designsystem.Base3
import ru.nativespeakers.core.designsystem.Primary0
import ru.nativespeakers.core.designsystem.Primary10
import ru.nativespeakers.core.designsystem.Primary2
import ru.nativespeakers.core.designsystem.Primary8
import ru.nativespeakers.core.model.CandidateNetwork
import ru.nativespeakers.core.ui.conditional
import ru.nativespeakers.core.ui.photo.PersonPhoto
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState
import ru.nativespeakers.core.ui.role.isHr

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppliedCandidateCard(
    candidate: CandidateNetwork,
    isLoading: Boolean,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = Primary2
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
            focusedElevation = 1.dp,
            pressedElevation = 1.dp,
            hoveredElevation = 1.dp,
            draggedElevation = 1.dp,
            disabledElevation = 1.dp
        ),
        modifier = modifier.width(108.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 8.dp, start = 4.dp, end = 4.dp, bottom = 4.dp)
        ) {
            PersonPhoto(
                state = candidate.toPersonAndPhotoUiState(),
                modifier = Modifier.size(46.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${candidate.name} ${candidate.surname}",
                style = MaterialTheme.typography.labelMedium,
                color = Primary10,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(10.dp))

            ApplyButton(
                onClick = onApplyClick,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ApplyButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(Primary0)
            .conditional(
                condition = !isLoading && isHr(),
                trueBlock = {
                    background(Primary0)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick
                        )
                },
                falseBlock = {
                    background(Base3)
                }
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Primary8,
                strokeWidth = 1.dp,
                modifier = Modifier.size(14.dp)
            )
        } else {
            Text(
                text = stringResource(R.string.feature_vacancy_common_apply),
                style = MaterialTheme.typography.labelLarge,
                fontSize = 10.sp,
                color = Primary8
            )
        }
    }
}