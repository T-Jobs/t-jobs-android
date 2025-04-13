package ru.nativespeakers.feature.competencies.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base3
import ru.nativespeakers.core.designsystem.Base5
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.designsystem.Primary7
import ru.nativespeakers.core.model.InterviewTypeNetwork
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetWithSearch
import ru.nativespeakers.core.ui.Competency
import ru.nativespeakers.core.ui.interview.SearchInterviewType
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.feature.competencies.R
import ru.nativespeakers.feature.profile.ui.ProfileViewModel

@Composable
fun CompetenciesScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    when {
        profileViewModel.userInfo.isLoading -> LoadingScreen()
        profileViewModel.userInfo.isError && !profileViewModel.userInfo.isLoaded -> ErrorScreen(
            onRetryButtonClick = profileViewModel::loadUserInfo
        )

        profileViewModel.userInfo.isLoaded -> CompetenciesScreenContent(
            profileViewModel = profileViewModel,
            navigateBack = navigateBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompetenciesScreenContent(
    profileViewModel: ProfileViewModel,
    navigateBack: () -> Unit,
) {
    val competenciesViewModel = hiltViewModel<CompetenciesViewModel>()

    var searchValue by rememberSaveable { mutableStateOf("") }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val selectedInterviewTypes = profileViewModel.userInfo.value.interviewTypeNetworks

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Header(
            onBackPressed = navigateBack,
            modifier = Modifier.fillMaxWidth()
        )
        CompetenciesSection(
            competencies = profileViewModel.userInfo.value.interviewTypeNetworks,
            onDeleteCompetencyClick = profileViewModel::deleteCompetencyById,
            onAddCompetencyClick = { showBottomSheet = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }

    if (showBottomSheet) {
        BottomSheetWithSearch(
            onValueChange = {
                searchValue = it
                competenciesViewModel.updateSearchInterviewTypes(it)
            },
            state = { competenciesViewModel.searchInterviewTypes.value },
            itemKey = { it.id },
            searchValue = { searchValue },
            itemComposable = {
                SearchInterviewType(
                    interviewType = it,
                    selected = it in selectedInterviewTypes,
                    onCheckedChange = {
                        if (it in selectedInterviewTypes) {
                            profileViewModel.deleteCompetencyById(it.id)
                        } else {
                            profileViewModel.addCompetency(it)
                        }
                    },
                    modifier = Modifier.animateItem()
                )
            },
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
private fun CompetenciesSection(
    competencies: List<InterviewTypeNetwork>,
    onDeleteCompetencyClick: (Long) -> Unit,
    onAddCompetencyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.feature_competencies_sections),
            style = MaterialTheme.typography.labelMedium,
            color = Base5,
            modifier = Modifier.padding(start = 16.dp)
        )

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
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            ) {
                items(
                    count = competencies.size,
                    key = { competencies[it].id }
                ) { i ->
                    Competency(
                        text = competencies[i].name,
                        withDraggableIcon = false,
                        withHelperIcon = true,
                        withDeleteIcon = true,
                        onDeleteClick = { onDeleteCompetencyClick(competencies[i].id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                    )
                    HorizontalDivider(
                        thickness = (0.5).dp,
                        color = Base3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    AddCompetencyButton(
                        paddingValues = if (competencies.isNotEmpty()) {
                            PaddingValues(top = 16.dp, bottom = 8.dp)
                        } else {
                            PaddingValues(top = 8.dp, bottom = 8.dp)
                        },
                        onClick = onAddCompetencyClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.feature_competencies_sections_description),
            style = MaterialTheme.typography.labelMedium,
            fontSize = 11.sp,
            color = Base5,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
private fun AddCompetencyButton(
    paddingValues: PaddingValues,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(paddingValues)
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
            tint = Primary7,
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = stringResource(R.string.feature_competencies_add_section),
            style = MaterialTheme.typography.bodyMedium,
            color = Primary7
        )
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
            text = stringResource(R.string.feature_competencies_competencies),
            style = MaterialTheme.typography.titleLarge,
            color = Base10,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}