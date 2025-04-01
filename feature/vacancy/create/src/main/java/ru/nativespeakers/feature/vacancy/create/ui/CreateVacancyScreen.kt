package ru.nativespeakers.feature.vacancy.create.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base3
import ru.nativespeakers.core.designsystem.Base4
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.model.InterviewTypeNetwork
import ru.nativespeakers.core.ui.BottomSheetWithSearch
import ru.nativespeakers.core.ui.Competency
import ru.nativespeakers.core.ui.SalaryPicker
import ru.nativespeakers.core.ui.TagGroup
import ru.nativespeakers.core.ui.interview.SearchInterviewType
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.feature.vacancy.create.R

@Composable
internal fun CreateVacancyScreen(
    createVacancyViewModel: CreateVacancyViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val availableTags = createVacancyViewModel.availableTags

    when {
        availableTags.isLoading -> LoadingScreen()
        availableTags.isError && !availableTags.isLoaded -> ErrorScreen(
            onRetryButtonClick = createVacancyViewModel::loadAvailableTags
        )

        availableTags.isLoaded -> CreateVacancyScreenContent(
            createVacancyViewModel = createVacancyViewModel,
            navigateBack = navigateBack
        )
    }

    LaunchedEffect(createVacancyViewModel.vacancyCreated) {
        if (createVacancyViewModel.vacancyCreated) {
            navigateBack()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateVacancyScreenContent(
    createVacancyViewModel: CreateVacancyViewModel,
    navigateBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            Header(
                onBackPressed = navigateBack,
                scrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        var showSearchInterviews by rememberSaveable { mutableStateOf(false) }
        var searchValue by rememberSaveable { mutableStateOf("") }
        val bottomSheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        val selectedInterviews = createVacancyViewModel.selectedInterviews.toList()

        val salaryHigherBoundState = rememberSliderState(
            steps = 1000,
            valueRange = 0f..1_000_000f
        )
        val salaryLowerBoundState = rememberSliderState(
            steps = 1000,
            valueRange = 0f..1_000_000f
        )

        val state = rememberReorderableLazyListState(
            onMove = { from, to ->
                createVacancyViewModel.reorderInterviews(from.index - 2, to.index - 2)
            }
        )

        LazyColumn(
            state = state.listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 30.dp),
            modifier = Modifier
                .padding(paddingValues)
                .reorderable(state)
                .detectReorderAfterLongPress(state)
        ) {
            item {
                Column {
                    InfoTextField(
                        title = stringResource(R.string.feature_vacancy_create_name),
                        value = createVacancyViewModel.name,
                        placeholderText = stringResource(R.string.feature_vacancy_create_name),
                        onValueChange = createVacancyViewModel::updateName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 30.dp)
                    )

                    InfoTextField(
                        title = stringResource(R.string.feature_vacancy_create_city),
                        value = createVacancyViewModel.city,
                        placeholderText = stringResource(R.string.feature_vacancy_create_name),
                        onValueChange = createVacancyViewModel::updateCity,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 30.dp)
                    )

                    InfoTextField(
                        title = stringResource(R.string.feature_vacancy_create_description),
                        value = createVacancyViewModel.description,
                        placeholderText = stringResource(R.string.feature_vacancy_create_start_writing),
                        onValueChange = createVacancyViewModel::updateDescription,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 30.dp),
                        textFieldModifier = Modifier.heightIn(min = 85.dp)
                    )

                    SalaryPicker(
                        title = stringResource(R.string.feature_vacancy_create_salary_lower_bound),
                        state = { salaryLowerBoundState },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(20.dp))

                    SalaryPicker(
                        title = stringResource(R.string.feature_vacancy_create_salary_higher_bound),
                        state = { salaryHigherBoundState },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(20.dp))

                    val tagGroups = createVacancyViewModel.availableTags.value
                    for ((group, tags) in tagGroups) {
                        TagGroup(
                            group = group,
                            tags = tags,
                            isTagSelected = { it in createVacancyViewModel.selectedTags },
                            onTagClick = {
                                if (it in createVacancyViewModel.selectedTags) {
                                    createVacancyViewModel.removeTag(it)
                                } else {
                                    createVacancyViewModel.addTag(it)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

            }

            trackSelector(
                state = state,
                selectedInterviews = selectedInterviews,
                onRemoveInterviewClick = createVacancyViewModel::removeInterview,
                onAddInterviewClick = { showSearchInterviews = true },
            )

            item {
                Button(
                    onClick = {
                        createVacancyViewModel.createVacancy(
                            salaryLowerBound = salaryLowerBoundState.value,
                            salaryHigherBound = salaryHigherBoundState.value
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
                        text = stringResource(R.string.feature_vacancy_create_complete),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (showSearchInterviews) {
            BottomSheetWithSearch(
                onValueChange = {
                    searchValue = it
                    createVacancyViewModel.updateSearchInterviewTypes(it)
                },
                state = { createVacancyViewModel.searchInterviewTypes.value },
                itemKey = { it.id },
                searchValue = { searchValue },
                itemComposable = {
                    SearchInterviewType(
                        interviewType = it,
                        selected = it in selectedInterviews,
                        onCheckedChange = {
                            if (it in selectedInterviews) {
                                createVacancyViewModel.removeInterview(it.id)
                            } else {
                                createVacancyViewModel.addInterview(it)
                            }
                        },
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
                text = stringResource(R.string.feature_vacancy_create_default_track),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.feature_vacancy_create_add),
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
                text = stringResource(R.string.feature_vacancy_create_interviews_empty),
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
                reorderableState = state,
                key = item.id,
            ) { isDragging ->
                val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
                Competency(
                    text = item.name,
                    withDraggableIcon = true,
                    withHelperIcon = false,
                    onDeleteClick = { onRemoveInterviewClick(item.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation)
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    onBackPressed: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.feature_vacancy_create_title),
                style = MaterialTheme.typography.titleLarge,
                color = Base10,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        navigationIcon = {
            IconButton(
                onClick = onBackPressed,
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Base8
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier.shadow(elevation = 4.dp)
    )
}