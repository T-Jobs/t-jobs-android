package ru.nativespeakers.feature.track.addinterview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.FindReplace
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base4
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.designsystem.Primary1
import ru.nativespeakers.core.designsystem.Primary10
import ru.nativespeakers.core.designsystem.Primary7
import ru.nativespeakers.core.model.InterviewTypeNetwork
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetWithSearch
import ru.nativespeakers.core.ui.date.SelectDateSection
import ru.nativespeakers.core.ui.interview.CopyPasteLinkSection
import ru.nativespeakers.core.ui.interview.SearchInterviewType
import ru.nativespeakers.core.ui.person.PersonCardWithRadioButton
import ru.nativespeakers.core.ui.person.toPersonCardUiState
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState
import ru.nativespeakers.core.ui.track.MembersSection
import ru.nativespeakers.feature.track.addinterview.R
import ru.nativespeakers.feature.track.common.InterviewCreateState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddInterviewScreen(
    viewModel: AddInterviewViewModel = hiltViewModel(),
    onCreateClick: (InterviewCreateState) -> Unit,
    navigateBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()

    var showSearchInterviewersBottomSheet by remember { mutableStateOf(false) }
    var searchInterviewersTypesValue by remember { mutableStateOf("") }
    val searchInterviewersBottomSheetState = rememberModalBottomSheetState()

    var showSearchInterviewTypesBottomSheet by remember { mutableStateOf(false) }
    var searchInterviewTypesValue by remember { mutableStateOf("") }
    val searchInterviewTypesBottomSheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            Header(
                onBackPressed = navigateBack,
                scrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SelectInterviewTypeSection(
                selectedInterviewType = viewModel.interviewCreateUiState.interviewType,
                onChangeButtonClick = { showSearchInterviewTypesBottomSheet = true },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            MembersSection(
                candidateUiState = viewModel.candidate,
                staffUiState = viewModel.interviewCreateUiState.interviewer?.toPersonAndPhotoUiState(),
                onAutoChooseClick = viewModel::setAutoInterviewer,
                onStaffChangeClick = { showSearchInterviewersBottomSheet = true },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            SelectDateSection(
                selectedDate = viewModel.interviewCreateUiState.date,
                onAutoChooseClick = { viewModel.updateDate(null) },
                onDateSelected = viewModel::updateDate,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            CopyPasteLinkSection(
                link = viewModel.interviewCreateUiState.link,
                onPasteLinkClick = viewModel::updateLink,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            CreateButton(
                onClick = {
                    onCreateClick(viewModel.interviewCreateUiState)
                    navigateBack()
                },
                enabled = viewModel.interviewCreateUiState.interviewType != null,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showSearchInterviewersBottomSheet) {
        BottomSheetWithSearch(
            state = { viewModel.searchInterviewers.value },
            sheetState = searchInterviewersBottomSheetState,
            onValueChange = {
                searchInterviewersTypesValue = it
                viewModel.updateSearchInterviewers(it)
            },
            itemKey = { it.id },
            searchValue = { searchInterviewersTypesValue },
            itemComposable = {
                PersonCardWithRadioButton(
                    state = it.toPersonCardUiState(),
                    selected = viewModel.interviewCreateUiState.interviewer == it,
                    onSelect = { viewModel.updateInterviewer(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            },
            onDismissRequest = {
                scope.launch { searchInterviewersBottomSheetState.hide() }.invokeOnCompletion {
                    if (!searchInterviewersBottomSheetState.isVisible) {
                        showSearchInterviewersBottomSheet = false
                    }
                }
            },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
    }

    if (showSearchInterviewTypesBottomSheet) {
        BottomSheetWithSearch(
            onValueChange = {
                searchInterviewTypesValue = it
                viewModel.updateSearchInterviewTypes(it)
            },
            state = { viewModel.searchInterviewTypes.value },
            itemKey = { it.id },
            searchValue = { searchInterviewTypesValue },
            itemComposable = {
                SearchInterviewType(
                    interviewType = it,
                    selected = it == viewModel.interviewCreateUiState.interviewType,
                    onCheckedChange = { viewModel.updateInterviewType(it) },
                    withCheckbox = false,
                    withRadioButton = true,
                    modifier = Modifier.animateItem()
                )
            },
            onDismissRequest = {
                scope.launch { searchInterviewTypesBottomSheetState.hide() }.invokeOnCompletion {
                    if (!searchInterviewTypesBottomSheetState.isVisible) {
                        showSearchInterviewTypesBottomSheet = false
                    }
                }
            },
            sheetState = searchInterviewTypesBottomSheetState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CreateButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContentColor = Base4,
            disabledContainerColor = Primary1,
        ),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.feature_track_addinterview_create),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun SelectInterviewTypeSection(
    selectedInterviewType: InterviewTypeNetwork?,
    onChangeButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        if (selectedInterviewType != null) {
            SelectedInterviewType(
                interviewType = selectedInterviewType,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Text(
                text = stringResource(R.string.feature_track_addinterview_choose_section),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 27.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = Base4,
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Primary1)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onChangeButtonClick,
                )
                .padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.FindReplace,
                contentDescription = null,
                tint = Primary7,
            )
        }
    }
}

@Composable
fun SelectedInterviewType(
    interviewType: InterviewTypeNetwork,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 2.dp,
            hoveredElevation = 2.dp,
            focusedElevation = 2.dp,
            draggedElevation = 2.dp,
            disabledElevation = 2.dp,
        ),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 7.dp, horizontal = 12.dp)
        ) {
            Text(
                text = interviewType.name,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = Primary10,
            )
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
                text = stringResource(R.string.feature_track_addinterview_title),
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