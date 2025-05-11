package ru.nativespeakers.feature.vacancy.alltracks.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.model.TrackNetwork
import ru.nativespeakers.core.ui.lifecycle.ResumedEventExecutor
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.core.ui.track.TrackCard
import ru.nativespeakers.core.ui.track.toTrackCardUiState
import ru.nativespeakers.feature.vacancy.alltracks.R

@Composable
internal fun AllTracksScreen(
    vacancyId: Long,
    navigateBack: () -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: AllTracksViewModel.Factory ->
            factory.create(vacancyId)
        }
    )

    ResumedEventExecutor(viewModel) {
        viewModel.loadTracks()
    }

    val state = viewModel.tracks
    when {
        state.isLoading && !state.isLoaded -> LoadingScreen()
        state.isError && !state.isLoaded -> ErrorScreen(
            onRetryButtonClick = viewModel::loadTracks
        )

        state.isLoaded -> AllTracksScreenContent(
            tracks = viewModel.tracks.value,
            navigateBack = navigateBack,
            navigateToTrackWithId = navigateToTrackWithId,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllTracksScreenContent(
    tracks: List<TrackNetwork>,
    navigateBack: () -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
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
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            items(tracks) {
                TrackCard(
                    state = it.toTrackCardUiState(),
                    onClick = { navigateToTrackWithId(it.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
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
                text = stringResource(R.string.feature_vacancy_alltracks_title),
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