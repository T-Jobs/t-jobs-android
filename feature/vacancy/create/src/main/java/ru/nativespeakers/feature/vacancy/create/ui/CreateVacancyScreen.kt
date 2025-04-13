package ru.nativespeakers.feature.vacancy.create.ui

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
import ru.nativespeakers.feature.vacancy.create.R

@Composable
internal fun CreateVacancyScreen(
    createVacancyViewModel: CreateVacancyViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val state = createVacancyViewModel.editVacancyUiState

    when {
        state.isLoading -> LoadingScreen()
        state.isError && !state.isLoaded -> ErrorScreen(
            onRetryButtonClick = createVacancyViewModel::loadData
        )

        state.isLoaded -> CreateVacancyScreenContent(
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
        EditVacancySection(
            editVacancyUiState = createVacancyViewModel.editVacancyUiState.value,
            searchInterviewTypes = { createVacancyViewModel.searchInterviewTypes.value },
            selectedInterviews = createVacancyViewModel.selectedInterviews,
            isTagSelected = { it in createVacancyViewModel.selectedTags },
            onTagClick = createVacancyViewModel::onTagClick,
            onRemoveInterviewClick = createVacancyViewModel::removeInterview,
            onVacancyNameChange = createVacancyViewModel::updateName,
            onCityValueChange = createVacancyViewModel::updateCity,
            onDescriptionValueChange = createVacancyViewModel::updateDescription,
            onReorderInterviews = createVacancyViewModel::reorderInterviews,
            onApplyVacancyClick = createVacancyViewModel::createVacancy,
            onSearchValueChange = createVacancyViewModel::updateSearchInterviewTypes,
            onSearchedInterviewTypeCheck = createVacancyViewModel::onSearchInterviewTypeClick,
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