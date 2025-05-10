package ru.nativespeakers.core.ui.interview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.nativespeakers.core.clipboard.getClipboardText
import ru.nativespeakers.core.clipboard.setClipboardText
import ru.nativespeakers.core.designsystem.Base1
import ru.nativespeakers.core.designsystem.Base6
import ru.nativespeakers.core.designsystem.Primary1
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.designsystem.Primary7
import ru.nativespeakers.core.ui.R

@Composable
fun CopyPasteLinkSection(
    link: String?,
    onPasteLinkClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
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
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                tint = Base6,
                contentDescription = null,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        val string = link ?: stringResource(R.string.core_ui_empty)
        Text(
            text = string,
            overflow = TextOverflow.Ellipsis,
            color = Base6,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        val clipboard = LocalClipboard.current
        ChangeButton(
            imageVector = Icons.Outlined.ContentPaste,
            text = null,
            onClick = {
                val text = clipboard.getClipboardText()
                val processedText = text?.trim()?.replace("\n", "")
                onPasteLinkClick(processedText)
            },
            enabled = enabled,
        )

        Spacer(modifier = Modifier.width(6.dp))

        ChangeButton(
            imageVector = Icons.Outlined.ContentCopy,
            text = null,
            onClick = { clipboard.setClipboardText(link) },
            enabled = enabled,
        )
    }
}

@Composable
private fun ChangeButton(
    imageVector: ImageVector,
    text: String?,
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
            imageVector = imageVector,
            tint = Primary6,
            contentDescription = null,
        )

        if (text != null) {
            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = text,
                color = Primary7,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}