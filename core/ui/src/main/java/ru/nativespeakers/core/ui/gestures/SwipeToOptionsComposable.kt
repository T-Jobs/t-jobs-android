package ru.nativespeakers.core.ui.gestures

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.nativespeakers.core.designsystem.Base0
import ru.nativespeakers.core.designsystem.Red7
import ru.nativespeakers.core.ui.R

@Composable
fun SwipeToDismissComposable(
    modifier: Modifier = Modifier,
    contentShape: Shape,
    onRemove: () -> Unit,
    content: @Composable () -> Unit,
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove()
                    true
                }
                else -> return@rememberSwipeToDismissBoxState false
            }
        },
        positionalThreshold = { it * 0.25f }
    )

    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            SwipeToDismissBackground(
                state = state,
                shape = contentShape,
                modifier = Modifier.fillMaxSize()
            )
        },
        enableDismissFromStartToEnd = false,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun SwipeToDismissBackground(
    shape: Shape,
    state:  SwipeToDismissBoxState,
    modifier: Modifier = Modifier
) {
    val color = when (state.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Red7.copy(alpha = 0.7f)
        SwipeToDismissBoxValue.StartToEnd -> Red7.copy(alpha = 0.7f)
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .background(color = color, shape = shape)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "delete",
                tint = Base0,
            )

            Text(
                text = stringResource(R.string.core_ui_delete),
                style = MaterialTheme.typography.labelMedium,
                color = Base0,
            )
        }
    }
}