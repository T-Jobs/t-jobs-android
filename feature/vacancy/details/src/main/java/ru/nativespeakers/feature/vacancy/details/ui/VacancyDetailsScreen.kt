package ru.nativespeakers.feature.vacancy.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base3
import ru.nativespeakers.core.designsystem.Base4
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.designsystem.Primary1
import ru.nativespeakers.core.designsystem.Primary5
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.model.CandidateNetwork
import ru.nativespeakers.core.model.InterviewBaseNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.model.TagCategoryNetwork
import ru.nativespeakers.core.model.TagNetwork
import ru.nativespeakers.core.model.TrackNetwork
import ru.nativespeakers.core.ui.Competency
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetOption
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetWithOptions
import ru.nativespeakers.core.ui.interview.PersonAndPhotoUiState
import ru.nativespeakers.core.ui.lifecycle.ResumedEventExecutor
import ru.nativespeakers.core.ui.photo.PersonPhoto
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState
import ru.nativespeakers.core.ui.role.isHr
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.core.ui.track.TrackCard
import ru.nativespeakers.core.ui.track.toTrackCardUiState
import ru.nativespeakers.core.ui.vacancy.SalaryBlock
import ru.nativespeakers.feature.home.InitialSearchCandidatesFilters
import ru.nativespeakers.feature.vacancy.common.AppliedCandidateCard
import ru.nativespeakers.feature.vacancy.details.R

@Composable
private fun rememberVacancyOptions(
    vacancyFollowedBuUser: Boolean,
    onFollowVacancyClick: () -> Unit,
    onEditVacancyClick: () -> Unit,
) = when {
    isHr() -> listOf(
        BottomSheetOption(
            leadingIcon = Icons.Outlined.Bookmark,
            name = if (vacancyFollowedBuUser) {
                stringResource(R.string.feature_vacancy_details_unfollow)
            } else {
                stringResource(R.string.feature_vacancy_details_follow)
            },
            onClick = onFollowVacancyClick
        ),
        BottomSheetOption(
            leadingIcon = Icons.Outlined.Edit,
            name = stringResource(R.string.feature_vacancy_details_edit),
            onClick = onEditVacancyClick
        ),
    )

    else -> emptyList()
}

@Composable
internal fun VacancyDetailsScreen(
    vacancyId: Long,
    navigateBack: () -> Unit,
    navigateToShowAllTracksScreen: () -> Unit,
    navigateToShowAllAppliedCandidatesScreen: () -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
    navigateToEditVacancyScreen: () -> Unit,
    navigateToRelevantResumeScreen: (InitialSearchCandidatesFilters) -> Unit,
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: VacancyDetailsViewModel.Factory ->
            factory.create(vacancyId)
        }
    )

    ResumedEventExecutor(viewModel) {
        viewModel.loadData()
    }

    val state = viewModel.vacancyDetailsUiState
    when {
        state.isLoading && !state.isLoaded -> LoadingScreen()
        state.isError && !state.isLoaded -> ErrorScreen(
            onRetryButtonClick = viewModel::loadData
        )

        state.isLoaded -> VacancyDetailsScreenContent(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToShowAllTracksScreen = navigateToShowAllTracksScreen,
            navigateToShowAllAppliedCandidatesScreen = navigateToShowAllAppliedCandidatesScreen,
            navigateToTrackWithId = navigateToTrackWithId,
            navigateToEditVacancyScreen = navigateToEditVacancyScreen,
            onFindRelevantResumesClick = navigateToRelevantResumeScreen,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VacancyDetailsScreenContent(
    viewModel: VacancyDetailsViewModel,
    navigateBack: () -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
    navigateToShowAllTracksScreen: () -> Unit,
    navigateToShowAllAppliedCandidatesScreen: () -> Unit,
    navigateToEditVacancyScreen: () -> Unit,
    onFindRelevantResumesClick: (InitialSearchCandidatesFilters) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val state = viewModel.vacancyDetailsUiState.value

    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val moreVacancyOptions = rememberVacancyOptions(
        vacancyFollowedBuUser = state.followedByUser,
        onFollowVacancyClick = viewModel::toggleFollowVacancy,
        onEditVacancyClick = navigateToEditVacancyScreen
    )

    Scaffold(
        topBar = {
            Header(
                onBackPressed = navigateBack,
                onMoreClick = {
                    showBottomSheet = true
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(vertical = 16.dp)
        ) {
            VacancyNameAndSalary(
                name = state.name,
                salaryLowerBound = state.salaryLowerBound,
                salaryHigherBound = state.salaryHigherBound,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            FindRelevantResumesButton(
                onClick = {
                    val filters = InitialSearchCandidatesFilters(
                        maxSalary = state.salaryHigherBound,
                        tagIds = state.tags.flatMap { it.value }.map { it.id },
                    )
                    onFindRelevantResumesClick(filters)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Text(
                text = state.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            TagsSection(
                tags = state.tags,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            StaffSection(
                role = stringResource(R.string.feature_vacancy_details_team_leads),
                staff = state.teamLeads,
                modifier = Modifier.fillMaxWidth()
            )

            StaffSection(
                role = stringResource(R.string.feature_vacancy_details_hrs),
                staff = state.hrs,
                modifier = Modifier.fillMaxWidth()
            )

            CurrentTracksSection(
                tracks = state.tracks,
                onTrackClick = navigateToTrackWithId,
                onAllClick = navigateToShowAllTracksScreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            AppliedCandidatesSection(
                candidates = state.appliedCandidates,
                onApplyCandidateClick = viewModel::applyCandidateWithId,
                isCandidateWithIdLoading = { it in viewModel.appliedCandidatesInProgress },
                onAllClick = navigateToShowAllAppliedCandidatesScreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            BaseTrackSection(
                interviewsBaseNetwork = state.baseTrack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }

    if (showBottomSheet) {
        BottomSheetWithOptions(
            options = moreVacancyOptions,
            onDismissRequest = {
                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            },
            sheetState = bottomSheetState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BaseTrackSection(
    interviewsBaseNetwork: List<InterviewBaseNetwork>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.feature_vacancy_details_default_track),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (interviewsBaseNetwork.isNotEmpty()) {
            for (interview in interviewsBaseNetwork) {
                Competency(
                    text = interview.interviewType.name,
                    withDraggableIcon = false,
                    withHelperIcon = true,
                    withDeleteIcon = false,
                    onDeleteClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Text(
                text = stringResource(R.string.feature_vacancy_details_list_is_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = Base4,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AppliedCandidatesSection(
    candidates: List<CandidateNetwork>,
    onApplyCandidateClick: (Long) -> Unit,
    isCandidateWithIdLoading: (Long) -> Boolean,
    onAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.feature_vacancy_details_applied_candidates),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.feature_vacancy_details_all),
                style = MaterialTheme.typography.labelLarge,
                color = Primary6,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAllClick
                )
            )
        }

        val firstThreeCandidates = candidates.take(3)
        if (firstThreeCandidates.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.SpaceAround,
                maxItemsInEachRow = 3,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (candidate in firstThreeCandidates) {
                    AppliedCandidateCard(
                        candidate = candidate,
                        isLoading = isCandidateWithIdLoading(candidate.id),
                        onApplyClick = { onApplyCandidateClick(candidate.id) }
                    )
                }
            }
        } else {
            Text(
                text = stringResource(R.string.feature_vacancy_details_list_is_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = Base4,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun CurrentTracksSection(
    tracks: List<TrackNetwork>,
    onTrackClick: (Long) -> Unit,
    onAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.feature_vacancy_details_current_tracks),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.feature_vacancy_details_all),
                style = MaterialTheme.typography.labelLarge,
                color = Primary6,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onAllClick
                    )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val firstThreeTracks = tracks.take(3)
        if (firstThreeTracks.isNotEmpty()) {
            for ((i, track) in firstThreeTracks.withIndex()) {
                TrackCard(
                    state = track.toTrackCardUiState(),
                    onClick = { onTrackClick(track.id) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (i != firstThreeTracks.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            Text(
                text = stringResource(R.string.feature_vacancy_details_list_is_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = Base4,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun StaffSection(
    role: String,
    staff: List<StaffNetwork>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Text(
            text = role,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(staff) {
                StaffItem(
                    personAndPhotoUiState = it.toPersonAndPhotoUiState()
                )
            }
        }

        if (staff.isEmpty()) {
            Text(
                text = stringResource(R.string.feature_vacancy_details_list_is_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = Base4,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
            )
        }
    }
}

@Composable
private fun StaffItem(
    personAndPhotoUiState: PersonAndPhotoUiState,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        PersonPhoto(
            state = personAndPhotoUiState,
            modifier = Modifier.size(26.dp)
        )

        Text(
            text = "${personAndPhotoUiState.name} ${personAndPhotoUiState.surname}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsSection(
    tags: Map<TagCategoryNetwork, List<TagNetwork>>,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 2.dp,
            focusedElevation = 2.dp,
            hoveredElevation = 2.dp,
            draggedElevation = 2.dp,
            disabledElevation = 2.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            for ((i, entry) in tags.entries.withIndex()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = entry.key.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        for (tag in entry.value) {
                            Tag(tag)
                        }
                    }
                }

                if (i != tags.entries.size - 1) {
                    HorizontalDivider(
                        thickness = (0.5).dp,
                        color = Base3,
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun Tag(
    tag: TagNetwork,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = tag.name,
            style = MaterialTheme.typography.bodyMedium,
            color = Primary1
        )
    }
}

@Composable
private fun FindRelevantResumesButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brush = Brush.horizontalGradient(listOf(Primary5, Color(0xFFEF4444)))

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .background(brush = brush, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.feature_vacancy_details_find_relevant_vacancies),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun VacancyNameAndSalary(
    name: String,
    salaryLowerBound: Int?,
    salaryHigherBound: Int?,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.displayMedium,
            fontSize = 27.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )

        SalaryBlock(
            salaryLowerBoundRub = salaryLowerBound,
            salaryHigherBoundRub = salaryHigherBound
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
                text = stringResource(R.string.feature_vacancy_details_title),
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