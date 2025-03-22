package ru.nativespeakers.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import ru.nativespeakers.core.designsystem.Base0
import ru.nativespeakers.core.designsystem.Primary2
import ru.nativespeakers.core.designsystem.Primary3
import ru.nativespeakers.core.designsystem.Primary4
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.designsystem.Primary8
import ru.nativespeakers.core.model.AppRole
import ru.nativespeakers.core.ui.LocalAppRoles
import ru.nativespeakers.core.ui.candidate.CandidateCard
import ru.nativespeakers.core.ui.interview.InterviewCard
import ru.nativespeakers.core.ui.paging.LazyPagingItemsColumn
import ru.nativespeakers.core.ui.photo.PersonPhoto
import ru.nativespeakers.core.ui.screen.EmptyScreen
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.core.ui.track.TrackCard
import ru.nativespeakers.core.ui.vacancy.VacancyCard
import ru.nativespeakers.feature.home.R
import ru.nativespeakers.feature.home.R.string as homeStrings

private enum class AvailableTab(val index: Int) {
    INTERVIEWS(0),
    VACANCIES(1),
    TRACKS(2),
}

@Composable
fun HomeScreen(
    navigateToProfile: () -> Unit,
    navigateToVacancyWithId: (Long) -> Unit,
    navigateToInterviewWithId: (Long) -> Unit,
    navigateToCandidateWithId: (Long) -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
    navigateToFilters: () -> Unit,
) {
    val homeViewModel = hiltViewModel<HomeViewModel>()

    when {
        homeViewModel.personAndPhotoUiState.isLoading -> LoadingScreen()
        homeViewModel.personAndPhotoUiState.isError -> {
            ErrorScreen(onRetryButtonClick = homeViewModel::loadUserInfo)
        }

        else -> {
            HomeScreenContent(
                homeViewModel = homeViewModel,
                navigateToProfile = navigateToProfile,
                navigateToVacancyWithId = navigateToVacancyWithId,
                navigateToInterviewWithId = navigateToInterviewWithId,
                navigateToCandidateWithId = navigateToCandidateWithId,
                navigateToTrackWithId = navigateToTrackWithId,
                navigateToFilters = navigateToFilters,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    homeViewModel: HomeViewModel,
    navigateToProfile: () -> Unit,
    navigateToVacancyWithId: (Long) -> Unit,
    navigateToInterviewWithId: (Long) -> Unit,
    navigateToCandidateWithId: (Long) -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
    navigateToFilters: () -> Unit,
) {
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val coroutineScope = rememberCoroutineScope()

    var currentTab by remember { mutableStateOf(AvailableTab.INTERVIEWS) }

    val vacanciesSearchItems = homeViewModel.searchVacanciesUiState.data.collectAsLazyPagingItems()
    val candidatesSearchItems =
        homeViewModel.searchCandidatesUiState.data.collectAsLazyPagingItems()

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            searchBarState = searchBarState,
            textFieldState = textFieldState,
            onSearch = {
                homeViewModel.updateSearchQuery(it)
                coroutineScope.launch { searchBarState.animateToCollapsed() }
            },
            placeholder = {
                Text(
                    text = stringResource(homeStrings.feature_home_search),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
            },
            leadingIcon = {
                if (searchBarState.currentValue == SearchBarValue.Expanded) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch { searchBarState.animateToCollapsed() }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Primary6
                        )
                    }
                } else {
                    PersonPhoto(
                        state = homeViewModel.personAndPhotoUiState.value,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = navigateToProfile
                            )
                    )
                }
            },
            trailingIcon = {
                if (searchBarState.currentValue == SearchBarValue.Expanded) {
                    IconButton(
                        onClick = navigateToFilters
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.page_info),
                            tint = Primary6,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            coroutineScope.launch { searchBarState.animateToCollapsed() }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "notification_icon",
                            tint = Primary8,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                unfocusedPlaceholderColor = Primary8,
                focusedPlaceholderColor = Primary4,
                unfocusedContainerColor = Primary3,
                focusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Header(
            searchBarState = searchBarState,
            currentTabSelected = currentTab,
            currentSearchTabSelected = { homeViewModel.currentSearchTabSelected },
            inputField = inputField,
            onInterviewClick = {
                currentTab = AvailableTab.INTERVIEWS
                if (!homeViewModel.relevantInterviewsUiState.isLoaded) {
                    homeViewModel.loadRelevantInterviews()
                }
            },
            onRelevantVacanciesClick = {
                currentTab = AvailableTab.VACANCIES
                if (!homeViewModel.relevantVacanciesUiState.isLoaded) {
                    homeViewModel.loadRelevantVacancies()
                }
            },
            onRelevantTracksClick = {
                currentTab = AvailableTab.TRACKS
            },
            modifier = Modifier.fillMaxWidth()
        )

        SelectedTabContent(
            homeViewModel = homeViewModel,
            currentTabSelected = currentTab,
            navigateToVacancyWithId = navigateToVacancyWithId,
            navigateToInterviewWithId = navigateToInterviewWithId,
            navigateToTrackWithId = navigateToTrackWithId,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )

        ExpandedFullScreenSearchBar(
            state = searchBarState,
            inputField = inputField,
            colors = SearchBarDefaults.colors(
                containerColor = Base0,
                dividerColor = Color.Transparent
            ),
        ) {
            TabRow(
                isSearchExpanded = true,
                currentTabSelected = currentTab,
                currentSearchTabSelected = { homeViewModel.currentSearchTabSelected },
                onCandidatesSearchClick = {
                    homeViewModel.updateSearchTabCategory(AvailableSearchTab.CANDIDATES)
                },
                onVacanciesSearchClick = {
                    homeViewModel.updateSearchTabCategory(AvailableSearchTab.VACANCIES)
                },
                onInterviewClick = {},
                onRelevantVacanciesClick = {},
                onRelevantTracksClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
            )

            if (homeViewModel.currentSearchTabSelected == AvailableSearchTab.VACANCIES) {
                LazyPagingItemsColumn(
                    items = vacanciesSearchItems,
                    isPullToRefreshActive = false,
                    itemKey = { it.id },
                    itemsEmptyComposable = {},
                    itemComposable = {
                        VacancyCard(
                            state = it,
                            onClick = { navigateToVacancyWithId(it.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    errorWhileRefreshComposable = {},
                    errorWhileAppendComposable = {},
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyPagingItemsColumn(
                    items = candidatesSearchItems,
                    isPullToRefreshActive = false,
                    itemKey = { it.id },
                    itemsEmptyComposable = {},
                    itemComposable = {
                        CandidateCard(
                            state = it,
                            onClick = { navigateToCandidateWithId(it.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    errorWhileRefreshComposable = {},
                    errorWhileAppendComposable = {},
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun SelectedTabContent(
    homeViewModel: HomeViewModel,
    currentTabSelected: AvailableTab,
    navigateToVacancyWithId: (Long) -> Unit,
    navigateToInterviewWithId: (Long) -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        when (currentTabSelected) {
            AvailableTab.VACANCIES -> {
                val state = homeViewModel.relevantVacanciesUiState
                val emptyVacanciesString = stringResource(homeStrings.feature_home_empty_vacancies)

                when {
                    state.isLoading -> LoadingScreen()
                    state.isError && !state.isLoaded -> ErrorScreen(
                        onRetryButtonClick = homeViewModel::loadRelevantVacancies
                    )

                    state.value.isEmpty() -> EmptyScreen(emptyVacanciesString)
                    else -> {
                        for (vacancy in state.value) {
                            VacancyCard(
                                state = vacancy,
                                onClick = { navigateToVacancyWithId(vacancy.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            AvailableTab.INTERVIEWS -> {
                val state = homeViewModel.relevantInterviewsUiState
                val emptyVacanciesString = stringResource(homeStrings.feature_home_empty_interviews)

                when {
                    state.isLoading -> LoadingScreen()
                    state.isError && !state.isLoaded -> ErrorScreen(
                        onRetryButtonClick = homeViewModel::loadRelevantInterviews
                    )

                    state.value.isEmpty() -> EmptyScreen(emptyVacanciesString)
                    else -> {
                        for (interview in state.value) {
                            InterviewCard(
                                interviewCardUiState = interview,
                                onClick = { navigateToInterviewWithId(interview.interviewId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            AvailableTab.TRACKS -> {
                val state = homeViewModel.relevantTracksUiState
                val emptyTracksString = stringResource(homeStrings.feature_home_empty_tracks)

                when {
                    state.isLoading -> LoadingScreen()
                    state.isError && !state.isLoaded -> ErrorScreen(
                        onRetryButtonClick = homeViewModel::loadRelevantTracks
                    )

                    state.value.isEmpty() -> EmptyScreen(emptyTracksString)
                    else -> {
                        for (track in state.value) {
                            TrackCard(
                                state = track,
                                onClick = { navigateToTrackWithId(track.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    searchBarState: SearchBarState,
    currentTabSelected: AvailableTab,
    currentSearchTabSelected: () -> AvailableSearchTab,
    inputField: @Composable () -> Unit,
    onInterviewClick: () -> Unit,
    onRelevantVacanciesClick: () -> Unit,
    onRelevantTracksClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.background(color = Primary2)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        SearchBar(
            state = searchBarState,
            inputField = inputField,
            shape = RoundedCornerShape(40.dp),
            colors = SearchBarDefaults.colors(
                dividerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        )
        TabRow(
            isSearchExpanded = searchBarState.currentValue == SearchBarValue.Expanded,
            currentTabSelected = currentTabSelected,
            currentSearchTabSelected = currentSearchTabSelected,
            onCandidatesSearchClick = {},
            onVacanciesSearchClick = {},
            onInterviewClick = onInterviewClick,
            onRelevantVacanciesClick = onRelevantVacanciesClick,
            onRelevantTracksClick = onRelevantTracksClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
        )
    }
}

/*@Composable
private fun SearchTextField(
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    userPhotoUiState: PersonAndPhotoUiState,
    onUserPhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = searchValue,
        onValueChange = onSearchValueChange,
        textStyle = MaterialTheme.typography.titleMedium,
        singleLine = true,
        shape = RoundedCornerShape(40.dp),
        leadingIcon = {
            PersonPhoto(
                state = userPhotoUiState,
                modifier = Modifier
                    .size(32.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onUserPhotoClick
                    )
            )
        },
        placeholder = {
            Text(
                text = stringResource(homeStrings.feature_home_search),
                style = MaterialTheme.typography.titleMedium,
                color = Primary8,
                maxLines = 1
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "notification_icon",
                tint = Primary8,
                modifier = Modifier.size(20.dp)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Primary10,
            focusedContainerColor = Primary2,
            focusedIndicatorColor = Color.Transparent,
            unfocusedTextColor = Primary10,
            unfocusedContainerColor = Primary3,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Primary8,
        ),
        modifier = modifier
    )
}*/

@Composable
private fun TabRow(
    isSearchExpanded: Boolean,
    currentTabSelected: AvailableTab,
    currentSearchTabSelected: () -> AvailableSearchTab,
    onVacanciesSearchClick: () -> Unit,
    onCandidatesSearchClick: () -> Unit,
    onInterviewClick: () -> Unit,
    onRelevantVacanciesClick: () -> Unit,
    onRelevantTracksClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val roles = LocalAppRoles.current
    if (AppRole.HR !in roles && AppRole.TEAM_LEAD !in roles && !isSearchExpanded) return

    var indicatorSize by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val selectedTabIndex = if (isSearchExpanded) {
        currentSearchTabSelected().index
    } else {
        currentTabSelected.index
    }

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
        val onFirstTextClick = if (isSearchExpanded) onCandidatesSearchClick else onInterviewClick
        val onSecondTextClick =
            if (isSearchExpanded) onVacanciesSearchClick else onRelevantVacanciesClick

        val firstText = if (isSearchExpanded) {
            stringResource(homeStrings.feature_home_candidates)
        } else {
            stringResource(homeStrings.feature_home_interview)
        }

        val secondText = if (isSearchExpanded) {
            stringResource(homeStrings.feature_home_vacancies)
        } else {
            stringResource(homeStrings.feature_home_vacancies)
        }

        Text(
            text = firstText,
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
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onFirstTextClick
                )
        )

        Text(
            text = secondText,
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
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSecondTextClick
                )
        )

        if (AppRole.HR in roles && !isSearchExpanded) {
            Text(
                text = stringResource(homeStrings.feature_home_tracks),
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
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onRelevantTracksClick
                    )
            )
        }
    }
}