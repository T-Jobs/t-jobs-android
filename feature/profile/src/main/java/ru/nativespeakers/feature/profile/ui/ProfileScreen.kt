package ru.nativespeakers.feature.profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.nativespeakers.core.designsystem.Base0
import ru.nativespeakers.core.designsystem.Base5
import ru.nativespeakers.core.ui.conditional
import ru.nativespeakers.core.ui.lifecycle.ResumedEventExecutor
import ru.nativespeakers.core.ui.photo.PersonPhoto
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState
import ru.nativespeakers.core.ui.role.isHr
import ru.nativespeakers.core.ui.role.isInterviewer
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.feature.profile.R

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToCompetenciesScreen: () -> Unit,
    navigateToCreateVacancyClick: () -> Unit,
) {
    val state = profileViewModel.userInfo

    ResumedEventExecutor(profileViewModel) {
        profileViewModel.loadUserInfo()
    }

    when {
        state.isLoading && !state.isLoaded -> LoadingScreen()
        !state.isLoaded && state.isError -> ErrorScreen(
            onRetryButtonClick = profileViewModel::loadUserInfo
        )

        state.isLoaded -> ProfileScreenContent(
            profileViewModel = profileViewModel,
            navigateBack = navigateBack,
            navigateToCompetenciesScreen = navigateToCompetenciesScreen,
            navigateToCreateVacancyClick = navigateToCreateVacancyClick
        )
    }
}

@Composable
private fun ProfileScreenContent(
    profileViewModel: ProfileViewModel,
    navigateBack: () -> Unit,
    navigateToCompetenciesScreen: () -> Unit,
    navigateToCreateVacancyClick: () -> Unit,
) {
    val userInfo = profileViewModel.userInfo.value

    Box {
        Image(
            painter = painterResource(R.drawable.feature_profile_header),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .safeDrawingPadding()
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TopBar(
                onBackPressed = navigateBack,
                onLogoutClick = profileViewModel::logout,
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
            )

            PersonPhoto(
                state = userInfo.toPersonAndPhotoUiState(),
                modifier = Modifier
                    .size(170.dp)
                    .border(width = 8.dp, color = Base0, shape = CircleShape)
            )

            Text(
                text = "${userInfo.name} ${userInfo.surname}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val rolesText = userInfo.roles.joinToString(separator = ", ")
            Text(
                text = rolesText,
                style = MaterialTheme.typography.titleMedium,
                color = Base5
            )

            SettingsOptions(
                interviewerModeValue = userInfo.isInterviewModeOn,
                onInterviewerModeClick = profileViewModel::updateInterviewMode,
                onCompetenciesClick = navigateToCompetenciesScreen,
                onCreateVacancyClick = navigateToCreateVacancyClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SettingsOptions(
    interviewerModeValue: Boolean,
    onInterviewerModeClick: (Boolean) -> Unit,
    onCreateVacancyClick: () -> Unit,
    onCompetenciesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
    ) {
        if (isInterviewer()) {
            SettingOption(
                isBoolean = true,
                text = stringResource(R.string.feature_profile_interviewer_mode),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.feature_profile_page_info),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                },
                selected = interviewerModeValue,
                onCheckedChange = onInterviewerModeClick,
                modifier = Modifier.fillMaxWidth()
            )

            SettingOption(
                isBoolean = false,
                text = stringResource(R.string.feature_profile_competencies),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.QuestionMark,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                },
                onClick = onCompetenciesClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (isHr()) {
            SettingOption(
                isBoolean = false,
                text = stringResource(R.string.feature_profile_create_vacancy),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                },
                onClick = onCreateVacancyClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TopBar(
    onBackPressed: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        IconButton(
            onClick = onBackPressed,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBackIosNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = stringResource(R.string.feature_profile_profile),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = onLogoutClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun SettingOption(
    isBoolean: Boolean,
    text: String,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .padding(horizontal = 8.dp)
            .conditional(!isBoolean) {
                clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick!!
                )
            }
    ) {
        leadingIcon()

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        if (isBoolean) {
            Switch(
                checked = selected!!,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    checkedBorderColor = Color.Transparent,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedTrackColor = Base5,
                    uncheckedBorderColor = Color.Transparent
                ),
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}