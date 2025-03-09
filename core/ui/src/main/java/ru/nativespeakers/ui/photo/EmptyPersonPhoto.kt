package ru.nativespeakers.ui.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.nativespeakers.core.designsystem.Blue4
import ru.nativespeakers.core.designsystem.Blue8

@Composable
fun EmptyPersonPhoto(
    name: String,
    surname: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(Blue4)
    ) {
        val firstNameLetter = name.first().uppercaseChar()
        val firstSurnameLetter = surname.first().uppercaseChar()
        Text(
            text = "$firstNameLetter$firstSurnameLetter",
            color = Blue8,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Preview
@Composable
private fun EmptyPersonPhotoPreview() {
    EmptyPersonPhoto(
        name = "Алексей",
        surname = "Трясков",
        modifier = Modifier.size(26.dp)
    )
}