package ru.nativespeakers.feature.vacancy.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.nativespeakers.core.designsystem.Base3
import ru.nativespeakers.core.designsystem.Base4
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.model.InterviewTypeNetwork
import ru.nativespeakers.core.model.TagCategoryNetwork
import ru.nativespeakers.core.model.TagNetwork
import ru.nativespeakers.core.ui.Competency
import ru.nativespeakers.core.ui.SalaryPicker
import ru.nativespeakers.core.ui.TagGroup
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetWithSearch
import ru.nativespeakers.core.ui.interview.SearchInterviewType
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState

@Immutable
data class EditVacancyUiState(
    val initialSalaryLowerBound: Int = 0,
    val initialSalaryHigherBound: Int = 0,
    val name: String = "",
    val city: String = "",
    val description: String = "",
    val tags: Map<TagCategoryNetwork, List<TagNetwork>> = emptyMap(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVacancySection(
    editVacancyUiState: EditVacancyUiState,
    searchInterviewTypes: () -> List<InterviewTypeNetwork>,
    selectedInterviews: List<InterviewTypeNetwork>,
    isTagSelected: (Long) -> Boolean,
    onTagClick: (Long) -> Unit,
    onRemoveInterviewClick: (Long) -> Unit,
    onVacancyNameChange: (String) -> Unit,
    onCityValueChange: (String) -> Unit,
    onDescriptionValueChange: (String) -> Unit,
    onReorderInterviews: (from: Int, to: Int) -> Unit,
    onApplyVacancyClick: (salaryLowerBound: Int, salaryHigherBound: Int) -> Unit,
    onSearchValueChange: (String) -> Unit,
    onSearchedInterviewTypeCheck: (InterviewTypeNetwork) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSearchInterviews by rememberSaveable { mutableStateOf(false) }
    var searchValue by rememberSaveable { mutableStateOf("") }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val salaryLowerBoundState = rememberSliderState(
        value = editVacancyUiState.initialSalaryLowerBound.toFloat(),
        steps = 1000,
        valueRange = 0f..1_000_000f
    )
    val salaryHigherBoundState = rememberSliderState(
        value = editVacancyUiState.initialSalaryHigherBound.toFloat(),
        steps = 1000,
        valueRange = 0f..1_000_000f
    )

    val lazyListState = rememberLazyListState()
    val state = rememberReorderableLazyListState(lazyListState) { from, to ->
        onReorderInterviews(from.index - 2, to.index - 2)
    }

    LazyColumn(
        state = lazyListState,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 30.dp),
        modifier = modifier
    ) {
        item {
            Column {
                InfoTextField(
                    title = stringResource(R.string.feature_vacancy_common_name),
                    value = editVacancyUiState.name,
                    placeholderText = stringResource(R.string.feature_vacancy_common_name),
                    onValueChange = onVacancyNameChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 30.dp)
                )

                InfoTextField(
                    title = stringResource(R.string.feature_vacancy_common_city),
                    value = editVacancyUiState.city,
                    placeholderText = stringResource(R.string.feature_vacancy_common_name),
                    onValueChange = onCityValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 30.dp)
                )

                InfoTextField(
                    title = stringResource(R.string.feature_vacancy_common_description),
                    value = editVacancyUiState.description,
                    placeholderText = stringResource(R.string.feature_vacancy_common_start_writing),
                    onValueChange = onDescriptionValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 30.dp),
                    textFieldModifier = Modifier.heightIn(min = 85.dp)
                )

                SalaryPicker(
                    title = stringResource(R.string.feature_vacancy_common_salary_lower_bound),
                    state = { salaryLowerBoundState },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(20.dp))

                SalaryPicker(
                    title = stringResource(R.string.feature_vacancy_common_salary_higher_bound),
                    state = { salaryHigherBoundState },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(20.dp))

                val tagGroups = editVacancyUiState.tags
                for ((group, tags) in tagGroups) {
                    TagGroup(
                        group = group,
                        tags = tags,
                        isTagSelected = isTagSelected,
                        onTagClick = onTagClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        }

        trackSelector(
            state = state,
            selectedInterviews = selectedInterviews,
            onRemoveInterviewClick = onRemoveInterviewClick,
            onAddInterviewClick = { showSearchInterviews = true },
        )

        item {
            Button(
                onClick = {
                    onApplyVacancyClick(
                        salaryLowerBoundState.value.toInt(),
                        salaryHigherBoundState.value.toInt()
                    )
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.feature_vacancy_common_complete),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (showSearchInterviews) {
        BottomSheetWithSearch(
            onValueChange = {
                searchValue = it
                onSearchValueChange(it)
            },
            state = searchInterviewTypes,
            itemKey = { it.id },
            searchValue = { searchValue },
            itemComposable = {
                SearchInterviewType(
                    interviewType = it,
                    selected = it in selectedInterviews,
                    onCheckedChange = { onSearchedInterviewTypeCheck(it) },
                    modifier = Modifier.animateItem()
                )
            },
            onDismissRequest = {
                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        showSearchInterviews = false
                    }
                }
            },
            sheetState = bottomSheetState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun LazyListScope.trackSelector(
    state: ReorderableLazyListState,
    selectedInterviews: List<InterviewTypeNetwork>,
    onAddInterviewClick: () -> Unit,
    onRemoveInterviewClick: (Long) -> Unit,
) {
    item {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.feature_vacancy_common_default_track),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.feature_vacancy_common_add),
                style = MaterialTheme.typography.labelLarge,
                color = Primary6,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAddInterviewClick
                )
            )
        }
        Spacer(Modifier.height(14.dp))
    }

    if (selectedInterviews.isEmpty()) {
        item {
            Text(
                text = stringResource(R.string.feature_vacancy_common_interviews_empty),
                style = MaterialTheme.typography.labelMedium,
                color = Base4,
                modifier = Modifier.padding(12.dp)
            )
        }
    } else {
        items(
            items = selectedInterviews,
            key = { it.id }
        ) { item ->
            ReorderableItem(
                state = state,
                key = item.id,
            ) { isDragging ->
                val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
                Competency(
                    text = item.name,
                    withDraggableIcon = true,
                    withHelperIcon = false,
                    withDeleteIcon = true,
                    onDeleteClick = { onRemoveInterviewClick(item.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation)
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp)
                        .draggableHandle()
                )
            }
        }
    }

    item {
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun InfoTextField(
    title: String,
    value: String,
    placeholderText: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Base3
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = textFieldModifier.fillMaxWidth()
        )
    }
}