package ru.nativespeakers.core.ui.track

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.nativespeakers.core.designsystem.Base1
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base6
import ru.nativespeakers.core.designsystem.Primary1
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.designsystem.Primary7
import ru.nativespeakers.core.designsystem.TJobTheme
import ru.nativespeakers.core.ui.R
import ru.nativespeakers.core.ui.interview.PersonAndPhotoUiState
import ru.nativespeakers.core.ui.photo.PersonPhoto

@Composable
fun TrackMemberSection(
    isStaff: Boolean,
    state: PersonAndPhotoUiState?,
    modifier: Modifier = Modifier,
    onChangeButtonClick: () -> Unit = {},
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
            )
        }
    }
}

@Composable
private fun ChangeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary1,
        ),
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