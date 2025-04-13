package ru.nativespeakers.feature.vacancy.appliedcandidates.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.feature.vacancy.appliedcandidates.R
import ru.nativespeakers.feature.vacancy.common.AppliedCandidateCard

@Composable
internal fun AppliedCandidatesScreen(
    vacancyId: Long,
    navigateBack: () -> Unit,
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: AppliedCandidatesViewModel.Factory ->
            factory.create(vacancyId)
        }
    )

    val state = viewModel.candidates
    when {
        state.isLoading -> LoadingScreen()
        state.isError && !state.isLoaded -> ErrorScreen(
            onRetryButtonClick = viewModel::loadCandidates
        )

        state.isLoaded -> AppliedCandidatesScreenContent(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppliedCandidatesScreenContent(
    viewModel: AppliedCandidatesViewModel,
    navigateBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            Header(
                onBackPressed = navigateBack,
                scrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxWidth()
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(108.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(viewModel.candidates.value) {
                AppliedCandidateCard(
                    candidate = it,
                    isLoading = it.id in viewModel.candidatesInProcess,
                    onApplyClick = { viewModel.applyCandidateWithId(it.id) }
                )
            }
        }
    }

    val errorMessage = stringResource(R.string.feature_vacancy_appliedcandidates_apply_error)
    LaunchedEffect(viewModel) {
        snapshotFlow { viewModel.isErrorWhileApplyingCandidate }
            .filter { it }
            .collectLatest {
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
                viewModel.onShowErrorWhileApplyingCandidateMessage()
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
                text = stringResource(R.string.feature_vacancy_appliedcandidates_title),
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