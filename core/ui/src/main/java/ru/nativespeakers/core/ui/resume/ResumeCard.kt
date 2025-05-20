package ru.nativespeakers.core.ui.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.nativespeakers.core.designsystem.Base6
import ru.nativespeakers.core.designsystem.Primary2
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.designsystem.TJobTheme
import ru.nativespeakers.core.model.ResumeNetwork
import ru.nativespeakers.core.model.TagCategoryNetwork
import ru.nativespeakers.core.model.TagNetwork
import ru.nativespeakers.core.ui.R

@Immutable
data class ResumeCardUiState(
    val id: Long = 0,
    val name: String = "",
    val firstThreeTags: List<TagNetwork> = emptyList(),
    val creationDate: LocalDateTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()),
)

@Composable
fun ResumeCard(
    state: ResumeCardUiState,
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = state.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.firstThreeTags.isNotEmpty()) {
                    for (tag in state.firstThreeTags) {
                        Tag(tag.name)
                        Spacer(Modifier.width(4.dp))
                    }

                    Spacer(Modifier.weight(1f))
                }

                Text(
                    text = stringResource(R.string.core_ui_open_form),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 10.sp,
                    color = Primary6,
                )
            }

            Date(state.creationDate)
        }
    }
}

@Composable
private fun Date(
    date: LocalDateTime,
    modifier: Modifier = Modifier
) {
    val day = "%02d".format(date.dayOfMonth)
    val month = "%02d".format(date.monthNumber)
    val year = date.year

    Text(
        text = "$day.$month.$year",
        style = MaterialTheme.typography.labelMedium,
        color = Base6,
        modifier = modifier
    )
}

@Composable
private fun Tag(
    tag: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(color = Primary2, shape = CircleShape)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Text(
            text = tag,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

fun ResumeNetwork.toResumeCardUiState() = ResumeCardUiState(
    id = id,
    name = name,
    firstThreeTags = tags.take(3),
    creationDate = date,
)

@Preview
@Composable
private fun ResumeCardPreview() = TJobTheme {
    ResumeCard(
        state = ResumeCardUiState(
            name = "Java-разработчик",
            firstThreeTags = listOf(
                TagNetwork(
                    id = 0,
                    category = TagCategoryNetwork(
                        id = 0,
                        name = ""
                    ),
                    name = "Java",
                ),
                TagNetwork(
                    id = 1,
                    category = TagCategoryNetwork(
                        id = 0,
                        name = ""
                    ),
                    name = "SQL",
                ),
                TagNetwork(
                    id = 2,
                    category = TagCategoryNetwork(
                        id = 0,
                        name = ""
                    ),
                    name = "Git",
                ),
            )
        ),
        onClick = {},
        modifier = Modifier.width(390.dp)
    )
}