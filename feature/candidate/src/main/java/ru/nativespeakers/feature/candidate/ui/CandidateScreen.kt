package ru.nativespeakers.feature.candidate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRowDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base5
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.designsystem.Primary0
import ru.nativespeakers.core.designsystem.Primary10
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.model.InterviewStatus
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetOption
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetWithMultipleItems
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetWithOptions
import ru.nativespeakers.core.ui.interview.PersonAndPhotoUiState
import ru.nativespeakers.core.ui.lifecycle.ResumedEventExecutor
import ru.nativespeakers.core.ui.photo.PersonPhoto
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState
import ru.nativespeakers.core.ui.resume.ResumeCard
import ru.nativespeakers.core.ui.resume.ResumeCardUiState
import ru.nativespeakers.core.ui.role.isHr
import ru.nativespeakers.core.ui.screen.EmptyScreen
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.core.ui.track.TrackCard
import ru.nativespeakers.core.ui.track.TrackCardUiState
import ru.nativespeakers.core.ui.vacancy.VacancyCard
import ru.nativespeakers.core.ui.vacancy.VacancyCardUiState
import ru.nativespeakers.core.ui.vacancy.VacancyCardWithApplyRejectButtons
import ru.nativespeakers.core.ui.vacancy.VacancyCardWithInviteOption
import ru.nativespeakers.feature.candidate.R

private enum class AvailableTab(val index: Int) {
    RESUMES(0),
    BRIEFS(1),
    TRACKS(2),
}

@Composable
private fun rememberCandidateOptions(
    onInviteCandidateClick: () -> Unit,
): List<BottomSheetOption> {
    return if (isHr()) {
        val inviteCandidate = stringResource(R.string.feature_candidate_invite_candidate)
        remember(onInviteCandidateClick) {
            listOf(
                BottomSheetOption(
                    name = inviteCandidate,
                    leadingIcon = Icons.Outlined.Add,
                    onClick = onInviteCandidateClick,
                )
            )
        }
    } else {
        emptyList()
    }
}

@Composable
internal fun CandidateScreen(
    candidateId: Long,
    navigateToResumeWithId: (Long) -> Unit,
    navigateToVacancyWithId: (Long) -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: CandidateViewModel.Factory ->
            factory.create(candidateId)
        },
    )

    ResumedEventExecutor(viewModel) {
        viewModel.loadData()
    }

    when {
        viewModel.candidate.isLoading && !viewModel.candidate.isLoaded -> LoadingScreen()
        !viewModel.candidate.isLoaded && viewModel.candidate.isError -> ErrorScreen(viewModel::loadData)
        viewModel.candidate.isLoaded -> CandidateScreenContent(
            viewModel = viewModel,
            navigateToResumeWithId = navigateToResumeWithId,
            navigateToVacancyWithId = navigateToVacancyWithId,
            navigateToTrackWithId = navigateToTrackWithId,
            navigateBack = navigateBack,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CandidateScreenContent(
    viewModel: CandidateViewModel,
    navigateToResumeWithId: (Long) -> Unit,
    navigateToVacancyWithId: (Long) -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
    navigateBack: () -> Unit,
) {
    var currentTab by rememberSaveable { mutableStateOf(AvailableTab.RESUMES) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val candidate = viewModel.candidate.value

    val optionsBottomSheetState = rememberModalBottomSheetState()
    var optionsBottomSheetVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var inviteCandidateBottomSheetVisible by remember { mutableStateOf(false) }
    val inviteCandidateBottomSheetState = rememberModalBottomSheetState()

    val candidateOptions = rememberCandidateOptions {
        scope.launch { optionsBottomSheetState.hide() }.invokeOnCompletion {
            if (!optionsBottomSheetState.isVisible) {
                optionsBottomSheetVisible = false
            }
        }

        inviteCandidateBottomSheetVisible = true
        if (!viewModel.vacanciesToInviteCandidate.isLoaded) {
            viewModel.loadVacanciesToInviteCandidate()
        }
    }

    Scaffold(
        topBar = {
            Header(
                onBackPressed = navigateBack,
                onMoreClick = {
                    if (candidateOptions.isNotEmpty()) {
                        optionsBottomSheetVisible = true
                    }
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                CandidateInfo(
                    state = candidate!!.toPersonAndPhotoUiState(),
                    tgId = candidate.tgId,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Primary0)
                        .padding(16.dp)
                )
            }

            item {
                TabRow(
                    currentTabSelected = currentTab,
                    onResumesClick = {
                        currentTab = AvailableTab.RESUMES
                        if (!viewModel.resumes.isLoaded) {
                            viewModel.loadResumes()
                        }
                    },
                    onBriefsClick = {
                        currentTab = AvailableTab.BRIEFS
                        if (!viewModel.briefs.isLoaded) {
                            viewModel.loadBriefs()
                        }
                    },
                    onTracksClick = {
                        currentTab = AvailableTab.TRACKS
                        if (!viewModel.tracks.isLoaded) {
                            viewModel.loadTracks()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Primary0)
                        .padding(horizontal = 16.dp)
                )
            }

            selectedTabContent(
                currentTabSelected = currentTab,
                viewModel = viewModel,
                onResumeClick = navigateToResumeWithId,
                onVacancyClick = navigateToVacancyWithId,
                onTrackClick = navigateToTrackWithId,
            )
        }
    }

    if (optionsBottomSheetVisible) {
        BottomSheetWithOptions(
            options = candidateOptions,
            onDismissRequest = {
                scope.launch { optionsBottomSheetState.hide() }.invokeOnCompletion {
                    if (!optionsBottomSheetState.isVisible) {
                        optionsBottomSheetVisible = false
                    }
                }
            },
            sheetState = optionsBottomSheetState,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (inviteCandidateBottomSheetVisible) {
        BottomSheetWithMultipleItems(
            state = { viewModel.vacanciesToInviteCandidate },
            onRetryLoad = viewModel::loadVacanciesToInviteCandidate,
            emptyStateMessage = stringResource(R.string.feature_candidate_invite_candidate_vacancies_empty),
            itemKey = { it.id },
            itemComposable = {
                VacancyCardWithInviteOption(
                    state = it,
                    onClick = {},
                    onInviteCandidateClick = {
                        viewModel.inviteCandidateOnVacancy(it.id)
                        scope.launch { inviteCandidateBottomSheetState.hide() }.invokeOnCompletion {
                            if (!inviteCandidateBottomSheetState.isVisible) {
                                inviteCandidateBottomSheetVisible = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            onDismissRequest = {
                scope.launch { inviteCandidateBottomSheetState.hide() }.invokeOnCompletion {
                    if (!inviteCandidateBottomSheetState.isVisible) {
                        inviteCandidateBottomSheetVisible = false
                    }
                }
            },
            sheetState = inviteCandidateBottomSheetState,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun LazyListScope.selectedTabContent(
    currentTabSelected: AvailableTab,
    viewModel: CandidateViewModel,
    onResumeClick: (Long) -> Unit,
    onVacancyClick: (Long) -> Unit,
    onTrackClick: (Long) -> Unit,
) {
    when (currentTabSelected) {
        AvailableTab.RESUMES -> {
            when {
                viewModel.resumes.isLoading && !viewModel.resumes.isLoaded -> item { LoadingScreen() }
                !viewModel.resumes.isLoaded && viewModel.resumes.isError -> item {
                    ErrorScreen(viewModel::loadResumes)
                }

                viewModel.resumes.isLoaded -> {
                    val resumes = viewModel.resumes.value
                    if (resumes.isEmpty()) {
                        item {
                            EmptyScreen(stringResource(R.string.feature_candidate_resumes_empty))
                        }
                    } else {
                        items(
                            items = resumes,
                            key = ResumeCardUiState::id
                        ) {
                            Spacer(Modifier.height(16.dp))
                            ResumeCard(
                                state = it,
                                onClick = { onResumeClick(it.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }

            }
        }

        AvailableTab.BRIEFS -> {
            when {
                viewModel.briefs.isLoading && !viewModel.briefs.isLoaded -> item { LoadingScreen() }
                !viewModel.briefs.isLoaded && viewModel.briefs.isError -> item {
                    ErrorScreen(viewModel::loadBriefs)
                }

                viewModel.briefs.isLoaded -> {
                    val briefs = viewModel.briefs.value
                    if (briefs.isEmpty()) {
                        item {
                            EmptyScreen(stringResource(R.string.feature_candidate_vacancies_empty))
                        }
                    } else {
                        items(
                            items = briefs,
                            key = VacancyCardUiState::id,
                        ) {
                            if (isHr()) {
                                Spacer(Modifier.height(16.dp))
                                VacancyCardWithApplyRejectButtons(
                                    state = it,
                                    onClick = { onVacancyClick(it.id) },
                                    onApplyClick = { viewModel.applyCandidate(it.id) },
                                    onRejectClick = { viewModel.rejectCandidate(it.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
                            } else {
                                Spacer(Modifier.height(16.dp))
                                VacancyCard(
                                    state = it,
                                    onClick = { onVacancyClick(it.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }

            }
        }

        AvailableTab.TRACKS -> {
            when {
                viewModel.tracks.isLoading && !viewModel.tracks.isLoaded -> item { LoadingScreen() }
                !viewModel.tracks.isLoaded && viewModel.tracks.isError -> item {
                    ErrorScreen(viewModel::loadTracks)
                }

                viewModel.tracks.isLoaded -> {
                    val tracks = viewModel.tracks.value
                    if (tracks.isEmpty()) {
                        item {
                            EmptyScreen(stringResource(R.string.feature_candidate_tracks_empty))
                        }
                    } else {
                        val trackGroups = tracks.groupBy {
                            it.lastInterviewStatus != InterviewStatus.FAILED &&
                                    it.lastInterviewStatus != InterviewStatus.PASSED
                        }

                        val active = trackGroups[true].orEmpty()
                        val history = trackGroups[false].orEmpty()

                        if (active.isNotEmpty()) {
                            item {
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.feature_candidate_tracks_active),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            items(
                                items = active,
                                key = TrackCardUiState::id
                            ) {
                                Spacer(Modifier.height(16.dp))
                                TrackCard(
                                    state = it,
                                    onClick = { onTrackClick(it.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
                            }
                        }

                        if (history.isNotEmpty()) {
                            item {
                                Spacer(Modifier.height(16.dp))

                                Text(
                                    text = stringResource(R.string.feature_candidate_tracks_history),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
                            }
                            items(
                                items = history,
                                key = TrackCardUiState::id
                            ) {
                                Spacer(Modifier.height(16.dp))
                                TrackCard(
                                    state = it,
                                    onClick = { onTrackClick(it.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(0.7f)
                                        .padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun TabRow(
    currentTabSelected: AvailableTab,
    onResumesClick: () -> Unit,
    onBriefsClick: () -> Unit,
    onTracksClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var indicatorSize by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val selectedTabIndex = currentTabSelected.index

    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                color = MaterialTheme.colorScheme.primary,
                width = indicatorSize,
                modifier = Modifier
                    .tabIndicatorOffset(selectedTabIndex, matchContentSize = true)
            )
        },
        divider = {},
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.feature_candidate_resumes),
            style = MaterialTheme.typography.bodyLarge,
            color = Primary6,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(10.dp)
                .onSizeChanged {
                    with(density) {
                        indicatorSize = (it.width / this.density.toInt()).dp / 2
                    }
                }
                .clickable(
                    interactionSource = remember(::MutableInteractionSource),
                    indication = null,
                    onClick = onResumesClick
                )
        )

        Text(
            text = stringResource(R.string.feature_candidate_briefs),
            style = MaterialTheme.typography.bodyLarge,
            color = Primary6,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(10.dp)
                .onSizeChanged {
                    with(density) {
                        indicatorSize = (it.width / this.density.toInt()).dp / 2
                    }
                }
                .clickable(
                    interactionSource = remember(::MutableInteractionSource),
                    indication = null,
                    onClick = onBriefsClick
                )
        )

        Text(
            text = stringResource(R.string.feature_candidate_tracks),
            style = MaterialTheme.typography.bodyLarge,
            color = Primary6,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(10.dp)
                .onSizeChanged {
                    with(density) {
                        indicatorSize = (it.width / this.density.toInt()).dp / 2
                    }
                }
                .clickable(
                    interactionSource = remember(::MutableInteractionSource),
                    indication = null,
                    onClick = onTracksClick
                )
        )
    }
}

@Composable
private fun CandidateInfo(
    state: PersonAndPhotoUiState,
    tgId: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        PersonPhoto(
            state = state,
            modifier = Modifier.size(140.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "${state.name} ${state.surname}",
            style = MaterialTheme.typography.titleLarge,
            color = Primary10,
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.feature_candidate_candidate),
            style = MaterialTheme.typography.labelMedium,
            color = Base5,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "@$tgId",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp,
            color = Primary6,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    onBackPressed: () -> Unit,
    onMoreClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.feature_candidate_title),
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
            if (isHr()) {
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