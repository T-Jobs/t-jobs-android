package ru.nativespeakers.feature.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.designsystem.Primary0
import ru.nativespeakers.core.ui.SalaryPicker
import ru.nativespeakers.core.ui.TagGroup
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.feature.home.ui.AvailableSearchTab
import ru.nativespeakers.feature.home.ui.FiltersUiState
import ru.nativespeakers.feature.home.ui.HomeViewModel

@Composable
fun FiltersScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    LaunchedEffect(homeViewModel) {
        if (!homeViewModel.availableFiltersUiState.isLoaded) {
            homeViewModel.loadAvailableTags()
        }
    }

    when {
        homeViewModel.availableFiltersUiState.isLoading -> LoadingScreen()
        !homeViewModel.availableFiltersUiState.isLoaded -> ErrorScreen(
            onRetryButtonClick = homeViewModel::loadAvailableTags
        )
        else -> FiltersScreenContent(
            homeViewModel = homeViewModel,
            onBackPressed = navigateBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersScreenContent(
    homeViewModel: HomeViewModel,
    onBackPressed: () -> Unit,
) {
    val selectedFiltersState = homeViewModel.selectedFiltersUiState.value
    val selectedTagIds = selectedFiltersState.tags.map { it.id }.toTypedArray()
    val selectedTags = remember { mutableStateListOf(*selectedTagIds) }

    val filtersState = homeViewModel.availableFiltersUiState.value
    val tagsGroups = filtersState.tags.groupBy { it.category }

    val salaryPickerState = rememberSliderState(
        value = selectedFiltersState.salary?.toFloat() ?: 0f,
        steps = 1000,
        valueRange = 0f..1_000_000f
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(color = Primary0)
    ) {
        Header(
            onBackPressed = onBackPressed,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        val title = if (homeViewModel.currentSearchTabSelected == AvailableSearchTab.VACANCIES) {
            stringResource(R.string.feature_filters_lower_salary_bound)
        } else {
            stringResource(R.string.feature_filters_high_salary_bound)
        }
        SalaryPicker(
            title = title,
            state = { salaryPickerState },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        for ((group, tags) in tagsGroups) {
            TagGroup(
                group = group,
                tags = tags,
                isTagSelected = { it in selectedTags },
                onTagClick = {
                    if (it in selectedTags) selectedTags.remove(it)
                    else selectedTags.add(it)
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Button(
            onClick = {
                val newFilters = FiltersUiState(
                    salary = salaryPickerState.value.toInt(),
                    tags = filtersState.tags.filter { it.id in selectedTags }
                )

                homeViewModel.updateCurrentSearchFilters(newFilters)
                onBackPressed()
            },
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.textButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.feature_filters_apply),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun Header(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(elevation = 4.dp)
            .background(color = MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        IconButton(
            onClick = onBackPressed,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBackIosNew,
                contentDescription = "Back",
                tint = Base8
            )
        }
        Text(
            text = stringResource(R.string.feature_filters_parameters),
            style = MaterialTheme.typography.titleLarge,
            color = Base10,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}