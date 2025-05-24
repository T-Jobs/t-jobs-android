package ru.nativespeakers.core.ui.vacancy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.nativespeakers.core.designsystem.Base0
import ru.nativespeakers.core.designsystem.Base6
import ru.nativespeakers.core.designsystem.Green5
import ru.nativespeakers.core.designsystem.Primary2
import ru.nativespeakers.core.designsystem.Primary8
import ru.nativespeakers.core.designsystem.Primary9
import ru.nativespeakers.core.designsystem.TJobTheme
import ru.nativespeakers.core.model.CandidateNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.model.VacancyNetwork
import ru.nativespeakers.core.ui.PrimaryAndSecondaryButtons
import ru.nativespeakers.core.ui.R
import ru.nativespeakers.core.ui.conditional
import ru.nativespeakers.core.ui.interview.PersonAndPhotoUiState
import ru.nativespeakers.core.ui.photo.PersonPhoto
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState

@Immutable
data class VacancyCardUiState(
    val id: Long = 0,
    val name: String = "",
    val city: String? = null,
    val firstTwoTags: List<String> = emptyList(),
    val firstTwoHrs: List<PersonAndPhotoUiState?> = emptyList(),
    val hrsCount: Int = 0,
    val firstTwoTeamLeads: List<PersonAndPhotoUiState?> = emptyList(),
    val teamLeadsCount: Int = 0,
    val firstTwoCandidates: List<PersonAndPhotoUiState?> = emptyList(),
    val candidatesCount: Int = 0,
    val salaryLowerBoundRub: Int? = null,
    val salaryHigherBoundRub: Int? = null,
)

@Composable
fun VacancyCardWithApplyRejectButtons(
    state: VacancyCardUiState,
    onClick: () -> Unit,
    onApplyClick: () -> Unit,
    onRejectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        VacancyCard(
            state = state,
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        )

        PrimaryAndSecondaryButtons(
            primaryText = stringResource(R.string.core_ui_open_apply),
            secondaryText = stringResource(R.string.core_ui_open_reject),
            onPrimaryClick = onApplyClick,
            onSecondaryClick = onRejectClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun VacancyCard(
    state: VacancyCardUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.large,
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
        Header(
            vacancyName = state.name,
            firstTwoTags = state.firstTwoTags,
            salaryLowerBoundRub = state.salaryLowerBoundRub,
            salaryHigherBoundRub = state.salaryHigherBoundRub,
            modifier = Modifier.padding(12.dp)
        )
        Persons(
            firstTwoHrs = state.firstTwoHrs,
            hrsCount = state.hrsCount,
            firstTwoTeamLeads = state.firstTwoTeamLeads,
            teamLeadsCount = state.teamLeadsCount,
            firstTwoCandidates = state.firstTwoCandidates,
            candidatesCount = state.candidatesCount,
            modifier = Modifier
                .fillMaxWidth()
                .conditional(state.city == null) {
                    padding(bottom = 12.dp)
                }
        )
        if (state.city != null) {
            Text(
                text = state.city,
                color = Primary8,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun Header(
    vacancyName: String,
    firstTwoTags: List<String>,
    salaryLowerBoundRub: Int?,
    salaryHigherBoundRub: Int?,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        Row {
            Text(
                text = vacancyName,
                style = MaterialTheme.typography.titleLarge,
                color = Primary8,
            )
            Spacer(modifier = Modifier.width(24.dp))
            if (firstTwoTags.isNotEmpty()) {
                Tag(firstTwoTags[0])
            }
            if (firstTwoTags.size > 1) {
                Spacer(modifier = Modifier.width(10.dp))
                Tag(firstTwoTags[1])
            }
        }
        SalaryBlock(
            salaryLowerBoundRub = salaryLowerBoundRub,
            salaryHigherBoundRub = salaryHigherBoundRub
        )
    }
}

@Composable
fun SalaryBlock(
    salaryLowerBoundRub: Int?,
    salaryHigherBoundRub: Int?,
    modifier: Modifier = Modifier
) {
    val salaryString = getStringForSalary(salaryLowerBoundRub, salaryHigherBoundRub)
    if (salaryString != null) {
        Row(modifier = modifier) {
            Text(
                text = "$salaryString ₽",
                color = Green5,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.core_ui_on_hands),
                color = Base6,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
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

@Composable
private fun Persons(
    firstTwoHrs: List<PersonAndPhotoUiState?>,
    hrsCount: Int,
    firstTwoTeamLeads: List<PersonAndPhotoUiState?>,
    teamLeadsCount: Int,
    firstTwoCandidates: List<PersonAndPhotoUiState?>,
    candidatesCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ) {
        PersonsBlock(
            who = stringResource(R.string.core_ui_hrs),
            firstTwoPersons = firstTwoHrs,
            personsCount = hrsCount,
            photoSize = 26.dp
        )
        PersonsBlock(
            who = stringResource(R.string.core_ui_team_leads),
            firstTwoPersons = firstTwoTeamLeads,
            personsCount = teamLeadsCount,
            photoSize = 26.dp
        )
        PersonsBlock(
            who = stringResource(R.string.core_ui_candidates),
            firstTwoPersons = firstTwoCandidates,
            personsCount = candidatesCount,
            photoSize = 26.dp
        )
    }
}

@Composable
private fun PersonsBlock(
    who: String,
    firstTwoPersons: List<PersonAndPhotoUiState?>,
    personsCount: Int,
    photoSize: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = "$who:",
            color = Base6,
            style = MaterialTheme.typography.labelMedium
        )
        PersonsPhotosOnTop(
            firstTwoPersons = firstTwoPersons,
            personsCount = personsCount,
            photoSize = photoSize
        )
    }
}

@Composable
private fun PersonsPhotosOnTop(
    firstTwoPersons: List<PersonAndPhotoUiState?>,
    personsCount: Int,
    photoSize: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy((-12).dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        PersonPhoto(
            state = if (firstTwoPersons.isNotEmpty()) firstTwoPersons[0] else null,
            modifier = Modifier.size(photoSize)
        )
        PersonPhoto(
            state = if (firstTwoPersons.size > 1) firstTwoPersons[1] else null,
            modifier = Modifier
                .border(width = 1.dp, color = Base0, shape = CircleShape)
                .size(photoSize + 2.dp)
        )
        if (personsCount > 0) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = Primary2, shape = CircleShape)
                    .height(26.dp)
                    .widthIn(min = 26.dp)
            ) {
                Text(
                    text = "+$personsCount",
                    color = Primary9,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

fun Int.moneyToString(): String {
    var tmp = this
    var cnt = 0
    val stringBuilder = StringBuilder()
    while (tmp > 0) {
        stringBuilder.append(tmp % 10)
        if (cnt % 3 == 2) {
            stringBuilder.append('.')
        }
        tmp /= 10
        cnt = (cnt + 1) % 3
    }

    if (stringBuilder.isEmpty())
        stringBuilder.append('0')

    if (stringBuilder.last() == '.')
        stringBuilder.deleteCharAt(stringBuilder.lastIndex)

    return stringBuilder.toString().reversed()
}

@Composable
private fun getStringForSalary(
    salaryLowerBoundRub: Int?,
    salaryHigherBoundRub: Int?,
) = when {
    salaryLowerBoundRub != null && salaryHigherBoundRub != null -> {
        "${salaryLowerBoundRub.moneyToString()} - ${salaryHigherBoundRub.moneyToString()}"
    }
    salaryLowerBoundRub != null -> "${stringResource(R.string.core_ui_from)} ${salaryLowerBoundRub.moneyToString()}"
    salaryHigherBoundRub != null -> "${stringResource(R.string.core_ui_until)} ${salaryHigherBoundRub.moneyToString()}"
    else -> null
}

@Preview
@Composable
private fun VacancyCardPreview_FullInfo() {
    TJobTheme {
        VacancyCard(
            state = VacancyCardUiState(
                name = "Java-разработчик",
                city = "Нижний Новгород",
                firstTwoTags = listOf("Java", "SQL"),
                firstTwoHrs = listOf(
                    PersonAndPhotoUiState(
                        name = "Анна",
                        surname = "Коренина",
                        photoUrl = null
                    ),
                    PersonAndPhotoUiState(
                        name = "Татьяна",
                        surname = "Ларина",
                        photoUrl = null
                    )
                ),
                hrsCount = 3,
                firstTwoTeamLeads = listOf(
                    PersonAndPhotoUiState(
                        name = "Андрй",
                        surname = "Болконский",
                        photoUrl = null
                    ),
                    PersonAndPhotoUiState(
                        name = "Евгений",
                        surname = "Онегин",
                        photoUrl = null
                    )
                ),
                teamLeadsCount = 5,
                firstTwoCandidates = listOf(
                    PersonAndPhotoUiState(
                        name = "Алексей",
                        surname = "Трясков",
                        photoUrl = null
                    ),
                    PersonAndPhotoUiState(
                        name = "Пьер",
                        surname = "Безухов",
                        photoUrl = null
                    )
                ),
                candidatesCount = 26,
                salaryLowerBoundRub = 70000,
                salaryHigherBoundRub = 120000,
            ),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun VacancyCardPreview_State1() {
    TJobTheme {
        VacancyCard(
            state = VacancyCardUiState(
                name = "Java-разработчик",
                city = "Нижний Новгород",
                firstTwoTags = listOf("Java"),
                firstTwoHrs = listOf(
                    PersonAndPhotoUiState(
                        name = "Анна",
                        surname = "Коренина",
                        photoUrl = null
                    ),
                ),
                hrsCount = 3,
                firstTwoTeamLeads = listOf(
                    PersonAndPhotoUiState(
                        name = "Андрй",
                        surname = "Болконский",
                        photoUrl = null
                    ),
                    PersonAndPhotoUiState(
                        name = "Евгений",
                        surname = "Онегин",
                        photoUrl = null
                    )
                ),
                teamLeadsCount = 0,
                firstTwoCandidates = listOf(
                    PersonAndPhotoUiState(
                        name = "Пьер",
                        surname = "Безухов",
                        photoUrl = null
                    )
                ),
                candidatesCount = 26,
                salaryLowerBoundRub = null,
                salaryHigherBoundRub = 120000,
            ),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun VacancyCardPreview_State2() {
    TJobTheme {
        VacancyCard(
            state = VacancyCardUiState(
                name = "Java-разработчик",
                city = null,
                firstTwoTags = emptyList(),
                firstTwoHrs = listOf(
                    PersonAndPhotoUiState(
                        name = "Анна",
                        surname = "Коренина",
                        photoUrl = null
                    ),
                ),
                hrsCount = 3,
                firstTwoTeamLeads = listOf(
                    PersonAndPhotoUiState(
                        name = "Андрй",
                        surname = "Болконский",
                        photoUrl = null
                    ),
                    PersonAndPhotoUiState(
                        name = "Евгений",
                        surname = "Онегин",
                        photoUrl = null
                    )
                ),
                teamLeadsCount = 0,
                firstTwoCandidates = listOf(
                    PersonAndPhotoUiState(
                        name = "Пьер",
                        surname = "Безухов",
                        photoUrl = null
                    )
                ),
                candidatesCount = 26,
                salaryLowerBoundRub = null,
                salaryHigherBoundRub = null,
            ),
            onClick = {},
        )
    }
}

fun vacancyCardUiState(
    vacancyNetwork: VacancyNetwork,
    candidates: List<CandidateNetwork>,
    teamLeads: List<StaffNetwork>,
    hrs: List<StaffNetwork>,
) = VacancyCardUiState(
    id = vacancyNetwork.id,
    name = vacancyNetwork.name,
    city = vacancyNetwork.town,
    firstTwoTags = vacancyNetwork.tags.take(2).map { it.name },
    firstTwoHrs = hrs
        .take(2)
        .map { it.toPersonAndPhotoUiState() },
    hrsCount = hrs.size,
    firstTwoTeamLeads = teamLeads
        .take(2)
        .map { it.toPersonAndPhotoUiState() },
    teamLeadsCount = teamLeads.size,
    firstTwoCandidates = candidates
        .take(2)
        .map { it.toPersonAndPhotoUiState() },
    candidatesCount = candidates.size,
    salaryLowerBoundRub = vacancyNetwork.salaryMin,
    salaryHigherBoundRub = vacancyNetwork.salaryMax
)