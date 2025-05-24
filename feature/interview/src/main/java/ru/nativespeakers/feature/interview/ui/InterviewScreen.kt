package ru.nativespeakers.feature.interview.ui

import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import ru.nativespeakers.core.designsystem.Base0
import ru.nativespeakers.core.designsystem.Base3
import ru.nativespeakers.core.designsystem.Base5
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.designsystem.Green6
import ru.nativespeakers.core.designsystem.Red6
import ru.nativespeakers.core.designsystem.Yellow2
import ru.nativespeakers.core.designsystem.Yellow4
import ru.nativespeakers.core.model.InterviewStatus
import ru.nativespeakers.core.ui.PrimaryAndSecondaryButtons
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetOption
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetWithOptions
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetWithSearch
import ru.nativespeakers.core.ui.date.SelectDateSection
import ru.nativespeakers.core.ui.interview.CopyPasteLinkSection
import ru.nativespeakers.core.ui.interview.InterviewStatusCard
import ru.nativespeakers.core.ui.lifecycle.ResumedEventExecutor
import ru.nativespeakers.core.ui.person.PersonCardWithRadioButton
import ru.nativespeakers.core.ui.person.toPersonCardUiState
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState
import ru.nativespeakers.core.ui.role.isHr
import ru.nativespeakers.core.ui.role.isInterviewer
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.core.ui.toUi
import ru.nativespeakers.core.ui.track.MembersSection
import ru.nativespeakers.feature.interview.R

@Composable
private fun rememberInterviewOptions(
    finished: Boolean,
    currentInterviewerId: Long?,
    currentUserId: Long,
    onCancelInterviewClick: () -> Unit,
    onRejectInterviewClick: () -> Unit,
    onSendFeedbackClick: () -> Unit,
): List<BottomSheetOption> {
    val options = mutableListOf<BottomSheetOption>()

    if (currentInterviewerId == currentUserId) {
        options += BottomSheetOption(
            name = stringResource(R.string.feature_interview_reject_interview),
            painter = painterResource(R.drawable.account_cancel_outline),
            onClick = onRejectInterviewClick,
        )

        if (!finished) {
            options += BottomSheetOption(
                name = stringResource(R.string.feature_interview_save_feedback),
                leadingIcon = Icons.Outlined.Balance,
                onClick = onSendFeedbackClick,
            )
        }
    }

    if (!finished && isHr()) {
        options += BottomSheetOption(
            name = stringResource(R.string.feature_interview_cancel_interview),
            leadingIcon = Icons.Outlined.Cancel,
            onClick = onCancelInterviewClick,
        )
    }

    return options
}

@Composable
private fun dateSelectionEnabled(
    interviewStatus: InterviewStatus,
) = interviewStatus != InterviewStatus.FAILED &&
        interviewStatus != InterviewStatus.PASSED &&
        isInterviewer()

@Composable
private fun staffSelectionEnabled(
    interviewStatus: InterviewStatus,
) = interviewStatus != InterviewStatus.FAILED &&
        interviewStatus != InterviewStatus.PASSED &&
        isHr()

@Composable
private fun feedbackEditEnabled(
    currentUserId: Long,
    currentInterviewerId: Long?,
    interviewStatus: InterviewStatus,
) = interviewStatus == InterviewStatus.WAITING_FOR_FEEDBACK && currentUserId == currentInterviewerId

@Composable
private fun linkEditEnabled(
    currentUserId: Long,
    currentInterviewerId: Long?,
    interviewStatus: InterviewStatus,
) = interviewStatus != InterviewStatus.FAILED &&
        interviewStatus != InterviewStatus.PASSED &&
        (currentUserId == currentInterviewerId || isHr())

@Composable
internal fun InterviewScreen(
    interviewId: Long,
    navigateBack: () -> Unit,
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: InterviewViewModel.Factory ->
            factory.create(interviewId)
        }
    )

    ResumedEventExecutor(viewModel) {
        viewModel.loadData()
    }

    when {
        viewModel.isLoading && !viewModel.isLoaded -> LoadingScreen()
        !viewModel.isLoaded -> ErrorScreen(viewModel::loadData)

        viewModel.isLoaded -> InterviewScreenContent(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InterviewScreenContent(
    viewModel: InterviewViewModel,
    navigateBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val interview = viewModel.interview.value
    val user = viewModel.user.value
    val candidate = viewModel.candidate.value
    val interviewer = viewModel.interviewer.value

    val scope = rememberCoroutineScope()

    var showSearchInterviewersBottomSheet by remember { mutableStateOf(false) }
    var searchValue by rememberSaveable { mutableStateOf("") }
    val searchInterviewersBottomSheetState = rememberModalBottomSheetState()

    var showInterviewOptionsBottomSheet by remember { mutableStateOf(false) }
    val interviewOptionsBottomSheetState = rememberModalBottomSheetState()
    val interviewOptions = rememberInterviewOptions(
        finished = interview.isFinished,
        currentInterviewerId = interviewer?.id,
        currentUserId = user.id,
        onCancelInterviewClick = viewModel::cancelInterview,
        onRejectInterviewClick = viewModel::rejectInterview,
        onSendFeedbackClick = {
            if (viewModel.feedbackUiState.feedback.isNotBlank()) {
                viewModel.sendFeedback()
            }
        },
    )

    Scaffold(
        topBar = {
            Header(
                interviewerId = interview.interviewerId,
                currentUserId = user.id,
                onBackPressed = navigateBack,
                onMoreClick = {
                    if (interviewOptions.isNotEmpty()) {
                        showInterviewOptionsBottomSheet = true
                    }
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .padding(paddingValues)
        ) {
            Text(
                text = interview.interviewType.name,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 27.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            InterviewStatusContent(
                status = interview.status,
                time = interview.datePicked,
                currentUserId = user.id,
                currentInterviewerId = interviewer?.id,
                onAcceptTimeClick = viewModel::approveTime,
                onDeclineTimeClick = viewModel::rejectTime,
                onFinishTrackClick = {
                    viewModel.finishTrack()
                    navigateBack()
                },
                onPassCandidateClick = viewModel::passCandidate,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            MembersSection(
                candidateUiState = candidate.toPersonAndPhotoUiState(),
                staffUiState = interviewer?.toPersonAndPhotoUiState(),
                onAutoChooseClick = { viewModel.updateInterviewer(null) },
                onStaffChangeClick = { showSearchInterviewersBottomSheet = true },
                staffChangeEnabled = staffSelectionEnabled(interview.status),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            SelectDateSection(
                selectedDate = interview.datePicked,
                onAutoChooseClick = viewModel::setAutoDate,
                onDateSelected = viewModel::updateDate,
                selectDateEnabled = dateSelectionEnabled(interview.status),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            CopyPasteLinkSection(
                link = interview.link,
                onPasteLinkClick = { it?.let(viewModel::updateLink) },
                enabled = linkEditEnabled(
                    currentUserId = user.id,
                    currentInterviewerId = interviewer?.id,
                    interviewStatus = interview.status,
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(22.dp))

            FeedbackSection(
                feedbackValue = viewModel.feedbackUiState.feedback,
                onFeedbackValueChange = viewModel::updateFeedback,
                positive = viewModel.feedbackUiState.positive,
                onFeedbackStatusChange = viewModel::updateFeedbackStatus,
                enabled = feedbackEditEnabled(
                    currentUserId = user.id,
                    currentInterviewerId = interviewer?.id,
                    interviewStatus = interview.status,
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showInterviewOptionsBottomSheet) {
        BottomSheetWithOptions(
            options = interviewOptions,
            onDismissRequest = {
                scope.launch { interviewOptionsBottomSheetState.hide() }.invokeOnCompletion {
                    if (!interviewOptionsBottomSheetState.isVisible) {
                        showInterviewOptionsBottomSheet = false
                    }
                }
            },
            sheetState = interviewOptionsBottomSheetState,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showSearchInterviewersBottomSheet) {
        BottomSheetWithSearch(
            state = { viewModel.searchInterviewers.value },
            sheetState = searchInterviewersBottomSheetState,
            onValueChange = {
                searchValue = it
                viewModel.updateSearchInterviewers(it)
            },
            itemKey = { it.id },
            searchValue = { searchValue },
            itemComposable = {
                PersonCardWithRadioButton(
                    state = it.toPersonCardUiState(),
                    selected = interviewer?.id == it.id,
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
}

@Composable
private fun InterviewStatusContent(
    status: InterviewStatus,
    time: LocalDateTime?,
    currentUserId: Long,
    currentInterviewerId: Long?,
    onAcceptTimeClick: () -> Unit,
    onDeclineTimeClick: () -> Unit,
    onFinishTrackClick: () -> Unit,
    onPassCandidateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        InterviewStatusCard(
            status = status,
            modifier = Modifier.fillMaxWidth()
        )

        if (status == InterviewStatus.WAITING_FOR_TIME_APPROVAL && time != null) {
            WaitingForTimeApprovalContent(
                time = time,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (status == InterviewStatus.WAITING_FOR_TIME_APPROVAL && currentUserId == currentInterviewerId) {
            PrimaryAndSecondaryButtons(
                primaryText = stringResource(R.string.feature_interview_approve),
                secondaryText = stringResource(R.string.feature_interview_cancel),
                onPrimaryClick = onAcceptTimeClick,
                onSecondaryClick = onDeclineTimeClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (status == InterviewStatus.FAILED && isHr()) {
            PrimaryAndSecondaryButtons(
                primaryText = stringResource(R.string.feature_interview_finish_track),
                secondaryText = stringResource(R.string.feature_interview_pass_candidate),
                onPrimaryClick = onFinishTrackClick,
                onSecondaryClick = onPassCandidateClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun WaitingForTimeApprovalContent(
    time: LocalDateTime,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(width = 1.dp, color = Yellow4, shape = MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(Yellow2)
            .padding(vertical = 12.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            tint = Yellow4,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = time.toUi(),
            textAlign = TextAlign.Center,
            color = Yellow4,
            style = MaterialTheme.typography.labelMedium.copy(fontStyle = FontStyle.Italic),
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun FeedbackSection(
    feedbackValue: String,
    onFeedbackValueChange: (String) -> Unit,
    positive: Boolean,
    onFeedbackStatusChange: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.feature_interview_feedback),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        FeedbackTab(
            positive = positive,
            onTabClick = onFeedbackStatusChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        FeedbackTextFile(
            value = feedbackValue,
            onValueChange = onFeedbackValueChange,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
private fun FeedbackTextFile(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        shape = MaterialTheme.shapes.medium,
        textStyle = MaterialTheme.typography.bodyMedium,
        placeholder = {
            Text(
                text = stringResource(R.string.feature_interview_start_write),
                color = Base5,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Base3,
            unfocusedBorderColor = Base3
        ),
        enabled = enabled,
        modifier = modifier
    )
}

@Composable
private fun FeedbackTab(
    positive: Boolean,
    onTabClick: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var negativeOffsetX by remember { mutableIntStateOf(0) }
    var selectedTabWidth by remember { mutableStateOf(0.dp) }
    val tabOffset by animateIntOffsetAsState(
        targetValue = if (positive) IntOffset.Zero else IntOffset(negativeOffsetX, 0),
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = Base3.copy(alpha = 0.3f), shape = RoundedCornerShape(10.dp))
            .padding(2.dp)
            .height(32.dp)
    ) {
        Box(
            modifier = Modifier
                .offset { tabOffset }
                .width(selectedTabWidth)
                .fillMaxHeight()
                .background(color = Base0, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .border(color = Base0.copy(alpha = 0.04f), width = (0.5).dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.feature_interview_positive),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = if (positive) Green6 else Base5,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.CenterVertically)
                    .onSizeChanged {
                        with(density) { selectedTabWidth = it.width.toDp() }
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { if (enabled) onTabClick(true) }
                    )
            )

            Text(
                text = stringResource(R.string.feature_interview_negative),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = if (!positive) Red6 else Base5,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.CenterVertically)
                    .onPlaced {
                        negativeOffsetX = it.positionInParent().x.toInt()
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { if (enabled) onTabClick(false) }
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    interviewerId: Long?,
    currentUserId: Long,
    onBackPressed: () -> Unit,
    onMoreClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {},
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
            if (isHr() || interviewerId == currentUserId) {
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