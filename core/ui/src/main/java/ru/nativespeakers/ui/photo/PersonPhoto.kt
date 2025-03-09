package ru.nativespeakers.ui.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import ru.nativespeakers.core.designsystem.Primary4
import ru.nativespeakers.core.designsystem.Primary8
import ru.nativespeakers.ui.AsyncImageWithLoading
import ru.nativespeakers.ui.interview.PersonAndPhotoUiState

@Composable
fun PersonPhoto(
    state: PersonAndPhotoUiState?,
    modifier: Modifier = Modifier
) {
    when {
        state == null -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .clip(CircleShape)
                    .background(Primary4)
            ) {
                Text(
                    text = "?",
                    color = Primary8,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
        state.photoUrl == null -> {
            EmptyPersonPhoto(
                name = state.name,
                surname = state.surname,
                modifier = modifier
            )
        }
        else -> {
            AsyncImageWithLoading(
                model = state.photoUrl,
                modifier = modifier.clip(CircleShape)
            )
        }
    }
}