package ru.nativespeakers.feature.vacancy.edit.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.feature.vacancy.common.EditVacancySection
import ru.nativespeakers.feature.vacancy.edit.R

@Composable
internal fun EditVacancyScreen(
    vacancyId: Long,
    navigateBack: () -> Unit,
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: EditVacancyViewModel.Factory ->
            factory.create(vacancyId)
        }
    )

    val state = viewModel.editVacancyUiState
    when {
        state.isLoading -> LoadingScreen()
        state.isError && !state.isLoaded -> ErrorScreen(
            onRetryButtonClick = viewModel::loadData
        )

        state.isLoaded -> EditVacancyScreenContent(
            viewModel = viewModel,
            navigateBack = navigateBack
        )
    }

    LaunchedEffect(viewModel.vacancyUpdated) {
        if (viewModel.vacancyUpdated) {
            navigateBack()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditVacancyScreenContent(
    viewModel: EditVacancyViewModel,
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
        EditVacancySection(
            editVacancyUiState = viewModel.editVacancyUiState.value,
            searchInterviewTypes = { viewModel.searchInterviewTypes.value },
            selectedInterviews = viewModel.selectedInterviews,
            isTagSelected = { it in viewModel.selectedTags },
            onTagClick = viewModel::onTagClick,
            onRemoveInterviewClick = viewModel::removeInterview,
            onVacancyNameChange = viewModel::updateName,
            onCityValueChange = viewModel::updateCity,
            onDescriptionValueChange = viewModel::updateDescription,
            onReorderInterviews = viewModel::reorderInterviews,
            onApplyVacancyClick = viewModel::updateVacancy,
            onSearchValueChange = viewModel::updateSearchInterviewTypes,
            onSearchedInterviewTypeCheck = viewModel::onSearchInterviewTypeClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                text = stringResource(R.string.feature_vacancy_edit_title),
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