package ru.nativespeakers.feature.track.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ru.nativespeakers.core.designsystem.Base0
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base5
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetOption
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetWithOptions
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetWithSearch
import ru.nativespeakers.core.ui.person.PersonCardWithRadioButton
import ru.nativespeakers.core.ui.person.toPersonCardUiState
import ru.nativespeakers.core.ui.gestures.SwipeToDismissComposable
import ru.nativespeakers.core.ui.interview.InterviewCard
import ru.nativespeakers.core.ui.interview.InterviewCardUiState
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState
import ru.nativespeakers.core.ui.role.isHr
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.core.ui.track.MembersSection
import ru.nativespeakers.core.ui.vacancy.VacancyCard
import ru.nativespeakers.feature.track.details.R

@Composable
private fun rememberTrackOptions(
    finished: Boolean,
    onFinishTrackClick: () -> Unit,
) = when {
    !finished && isHr() -> listOf(
        BottomSheetOption(
            painter = painterResource(R.drawable.feature_track_details_flag_checkered),
            name = stringResource(R.string.feature_track_details_finish_track),
            onClick = onFinishTrackClick,
        ),
    )

    else -> emptyList()
}

@Composable
internal fun TrackScreen(
    trackId: Long,
    navigateBack: () -> Unit,
    navigateToVacancyWithId: (Long) -> Unit,
    navigateToInterviewWithId: (Long) -> Unit,
    navigateToAddInterview: () -> Unit,
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: TrackViewModel.Factory ->
            factory.create(trackId)
        }
    )

    val state = viewModel.trackDetailsUiState
    when {
        state.isLoading -> LoadingScreen()
        !state.isLoaded && state.isError -> ErrorScreen(viewModel::loadData)
        state.isLoaded -> TrackScreenContent(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToVacancyWithId = navigateToVacancyWithId,
            navigateToInterviewWithId = navigateToInterviewWithId,
            navigateToAddInterview = navigateToAddInterview,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackScreenContent(
    viewModel: TrackViewModel,
    navigateBack: () -> Unit,
    navigateToVacancyWithId: (Long) -> Unit,
    navigateToInterviewWithId: (Long) -> Unit,
    navigateToAddInterview: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state = viewModel.trackDetailsUiState.value

    val trackOptions = rememberTrackOptions(
        finished = state.finished,
        onFinishTrackClick = viewModel::finishTrack,
    )
    var showOptionsBottomSheet by remember { mutableStateOf(false) }
    val optionsBottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var showSearchHrsBottomSheet by remember { mutableStateOf(false) }
    var searchValue by rememberSaveable { mutableStateOf("") }
    val searchHrsBottomSheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            Header(
                finished = state.finished,
                onBackPressed = navigateBack,
                onMoreClick = { showOptionsBottomSheet = true },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                MembersSection(
                    candidateUiState = state.candidateNetwork.toPersonAndPhotoUiState(),
                    staffUiState = state.hr.toPersonAndPhotoUiState(),
                    onHrChangeClick = { showSearchHrsBottomSheet = true },
                )
            }

            item {
                VacancyCard(
                    state = state.vacancy,
                    onClick = { navigateToVacancyWithId(state.vacancy.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            interviewsSection(
                interviews = state.interviews,
                onAddInterviewClick = navigateToAddInterview,
                onInterviewWithIdClick = navigateToInterviewWithId,
                onRemoveInterviewById = viewModel::removeInterviewById,
            )
        }
    }

    if (showOptionsBottomSheet) {
        BottomSheetWithOptions(
            options = trackOptions,
            onDismissRequest = {
                scope.launch { optionsBottomSheetState.hide() }.invokeOnCompletion {
                    if (!optionsBottomSheetState.isVisible) {
                        showOptionsBottomSheet = false
                    }
                }
            },
            sheetState = optionsBottomSheetState,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showSearchHrsBottomSheet) {
        BottomSheetWithSearch(
            state = { viewModel.searchHrs.value },
            sheetState = searchHrsBottomSheetState,
            onValueChange = {
                searchValue = it
                viewModel.updateSearchHrs(it)
            },
            itemKey = { it.id },
            searchValue = { searchValue },
            itemComposable = {
                PersonCardWithRadioButton(
                    state = it.toPersonCardUiState(),
                    selected = viewModel.currentSelectedHrId == it.id,
                    onSelect = { viewModel.changeHr(it.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            },
            onDismissRequest = {
                scope.launch { searchHrsBottomSheetState.hide() }.invokeOnCompletion {
                    if (!searchHrsBottomSheetState.isVisible) {
                        showSearchHrsBottomSheet = false
                    }
                }
            },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
    }
}

private fun LazyListScope.interviewsSection(
    interviews: List<InterviewCardUiState>,
    onAddInterviewClick: () -> Unit,
    onInterviewWithIdClick: (Long) -> Unit,
    onRemoveInterviewById: (Long) -> Unit,
) {
    item {
        InterviewsSectionTitle(
            interviewsCount = interviews.size,
            onAddInterviewClick = onAddInterviewClick,
            modifier = Modifier.fillMaxWidth()
        )
    }

    items(
        items = interviews,
        key = { it.interviewId }
    ) {
        SwipeToDismissComposable(
            contentShape = MaterialTheme.shapes.medium,
            onRemove = { onRemoveInterviewById(it.interviewId) },
            modifier = Modifier
                .fillMaxWidth()
                .animateItem()
        ) {
            InterviewCard(
                interviewCardUiState = it,
                onClick = { onInterviewWithIdClick(it.interviewId) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InterviewsSectionTitle(
    interviewsCount: Int,
    onAddInterviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.feature_track_details_meetings),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.width(3.dp))

        Text(
            text = "• $interviewsCount",
            style = MaterialTheme.typography.titleLarge,
            color = Base5,
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                .clickable(onClick = onAddInterviewClick)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = Base0,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    finished: Boolean,
    onBackPressed: () -> Unit,
    onMoreClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.feature_track_details_title),
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
        actions = {
            if (isHr() && !finished) {
                IconButton(
                    onClick = onMoreClick,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreHoriz,
                        contentDescription = "Back",
                        tint = Base8
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier.shadow(elevation = 4.dp)
    )
}