package ru.nativespeakers.core.ui.track

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.nativespeakers.core.designsystem.Base1
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base6
import ru.nativespeakers.core.designsystem.Primary1
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.designsystem.Primary7
import ru.nativespeakers.core.designsystem.TJobTheme
import ru.nativespeakers.core.ui.R
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetOption
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetWithOptions
import ru.nativespeakers.core.ui.interview.PersonAndPhotoUiState
import ru.nativespeakers.core.ui.photo.PersonPhoto

@Composable
private fun rememberStaffChangeOptions(
    onAutoChooseClick: () -> Unit,
    onFindStaffClick: () -> Unit,
): List<BottomSheetOption> {
    val autoChooseString = stringResource(R.string.core_ui_auto_choose)
    val chooseByHandString = stringResource(R.string.core_ui_choose_by_hand)
    return remember(onAutoChooseClick, onFindStaffClick) {
        listOf(
            BottomSheetOption(
                name = autoChooseString,
                leadingIcon = Icons.Outlined.Settings,
                onClick = onAutoChooseClick,
            ),
            BottomSheetOption(
                name = chooseByHandString,
                leadingIcon = Icons.Outlined.Search,
                onClick = onFindStaffClick,
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersSection(
    candidateUiState: PersonAndPhotoUiState,
    staffUiState: PersonAndPhotoUiState?,
    onAutoChooseClick: () -> Unit,
    onStaffChangeClick: () -> Unit,
    modifier: Modifier = Modifier,
    enableAutoSelection: Boolean = true,
    staffChangeEnabled: Boolean = true,
) {
    var bottomSheetVisible by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        TrackMemberSection(
            isStaff = false,
            state = candidateUiState,
            modifier = Modifier.fillMaxWidth()
        )

        TrackMemberSection(
            isStaff = true,
            state = staffUiState,
            onChangeButtonClick = {
                if (enableAutoSelection) {
                    bottomSheetVisible = true
                } else {
                    onStaffChangeClick()
                }
            },
            changeButtonEnabled = staffChangeEnabled,
            modifier = Modifier.fillMaxWidth()
        )
    }

    val options = rememberStaffChangeOptions(
        onAutoChooseClick = {
            onAutoChooseClick()
            bottomSheetVisible = false
        },
        onFindStaffClick = {
            onStaffChangeClick()
            bottomSheetVisible = false
        }
    )

    if (bottomSheetVisible && enableAutoSelection) {
        BottomSheetWithOptions(
            options = options,
            onDismissRequest = {
                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        bottomSheetVisible = false
                    }
                }
            },
            sheetState = bottomSheetState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TrackMemberSection(
    isStaff: Boolean,
    state: PersonAndPhotoUiState?,
    modifier: Modifier = Modifier,
    onChangeButtonClick: () -> Unit = {},
    changeButtonEnabled: Boolean = true,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(Base1)
                .padding(10.dp)
        ) {
            val drawableRes = if (isStaff) {
                R.drawable.core_ui_account_hard_hat_outline
            } else {
                R.drawable.core_ui_account_file_outline
            }
            Icon(
                painter = painterResource(drawableRes),
                tint = Base6,
                contentDescription = null,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        PersonPhoto(
            state = state,
            modifier = Modifier.size(44.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        val string = state?.let {
            "${it.name} ${it.surname}"
        } ?: stringResource(R.string.core_ui_auto)
        Text(
            text = string,
            overflow = TextOverflow.Ellipsis,
            color = if (state == null) Base6 else Base10,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        if (isStaff) {
            ChangeButton(
                onClick = onChangeButtonClick,
                enabled = changeButtonEnabled,
            )
        }
    }
}

@Composable
private fun ChangeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary1,
        ),
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.core_ui_find_replace),
            tint = Primary6,
            contentDescription = null,
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = stringResource(R.string.core_ui_replace),
            color = Primary7,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview
@Composable
private fun TrackMemberSectionPreview() {
    TJobTheme {
        TrackMemberSection(
            isStaff = true,
            state = PersonAndPhotoUiState(
                name = "Андрей",
                surname = "Болконский",
            ),
            modifier = Modifier.width(370.dp)
        )
    }
}