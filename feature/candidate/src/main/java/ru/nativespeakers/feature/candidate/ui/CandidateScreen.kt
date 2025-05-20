package ru.nativespeakers.feature.candidate.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.designsystem.Primary10
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.ui.interview.PersonAndPhotoUiState
import ru.nativespeakers.core.ui.lifecycle.ResumedEventExecutor
import ru.nativespeakers.core.ui.photo.PersonPhoto
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState
import ru.nativespeakers.core.ui.role.isHr
import ru.nativespeakers.core.ui.role.isTeamLead
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.feature.candidate.R

private enum class AvailableTab(val index: Int) {
    RESUMES(0),
    BRIEFS(1),
    TRACKS(2),
}

@Composable
internal fun CandidateScreen(
    candidateId: Long,
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
        viewModel.isLoading && !viewModel.isLoaded -> LoadingScreen()
        !viewModel.isLoaded -> ErrorScreen(viewModel::loadData)
        viewModel.isLoaded -> CandidateScreenContent(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CandidateScreenContent(
    viewModel: CandidateViewModel,
    navigateBack: () -> Unit,
) {
    var currentTab by rememberSaveable { mutableStateOf(AvailableTab.RESUMES) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val candidate = viewModel.candidate.value
    val resumes = viewModel.resumes.value

    Scaffold(
        topBar = {
            Header(
                onBackPressed = navigateBack,
                onMoreClick = {},
                scrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .padding(paddingValues)
        ) {
            item {
                CandidateInfo(
                    state = candidate!!.toPersonAndPhotoUiState(),
                    tgId = candidate.tgId,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                TabRow(
                    currentTab = currentTab,
                    onResumesClick = {
                        
                    }
                )
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
            style = MaterialTheme.typography.titleLarge,
            color = Primary10,
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