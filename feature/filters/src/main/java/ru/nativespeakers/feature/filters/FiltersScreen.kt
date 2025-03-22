package ru.nativespeakers.feature.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.designsystem.Primary0
import ru.nativespeakers.core.designsystem.Primary1
import ru.nativespeakers.core.designsystem.Primary2
import ru.nativespeakers.core.designsystem.Primary4
import ru.nativespeakers.core.designsystem.Primary8
import ru.nativespeakers.core.model.TagCategoryNetwork
import ru.nativespeakers.core.model.TagNetwork
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.core.ui.vacancy.moneyToString
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
    val selectedTags = remember { mutableStateSetOf(*selectedTagIds) }

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
        SalaryPicker(
            currentSearchTab = { homeViewModel.currentSearchTabSelected },
            state = { salaryPickerState }
        )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalaryPicker(
    currentSearchTab: () -> AvailableSearchTab,
    state: () -> SliderState,
    modifier: Modifier = Modifier
) {
    val title = if (currentSearchTab() == AvailableSearchTab.VACANCIES) {
        stringResource(R.string.feature_filters_lower_salary_bound)
    } else {
        stringResource(R.string.feature_filters_high_salary_bound)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Slider(
            state = state(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Primary2
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = state().valueRange.start.toInt().moneyToString(),
                color = Primary8,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = state().valueRange.endInclusive.toInt().moneyToString(),
                color = Primary8,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun TagGroup(
    group: TagCategoryNetwork,
    tags: List<TagNetwork>,
    isTagSelected: (Long) -> Boolean,
    onTagClick: (Long) -> Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = group.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (tag in tags) {
                FilterChip(
                    selected = isTagSelected(tag.id),
                    onClick = { onTagClick(tag.id) },
                    label = {
                        Text(
                            text = tag.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    shape = CircleShape,
                    border = null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedLabelColor = Primary1,
                        labelColor = Primary4,
                        containerColor = Primary1,
                        selectedContainerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}