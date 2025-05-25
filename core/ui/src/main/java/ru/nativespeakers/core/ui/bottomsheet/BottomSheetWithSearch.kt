package ru.nativespeakers.core.ui.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.nativespeakers.core.designsystem.Base5
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.core.ui.R
import ru.nativespeakers.core.ui.screen.EmptyScreen
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> BottomSheetWithSearch(
    onValueChange: (String) -> Unit,
    state: () -> List<T>,
    itemKey: (T) -> Any,
    searchValue: () -> String,
    itemComposable: @Composable LazyItemScope.(T) -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
) {
    ModalBottomSheet(
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                width = 70.dp,
                shape = MaterialTheme.shapes.medium,
                color = Color(0xFFD9D9D9),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
            )
        },
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.large.copy(
            bottomEnd = CornerSize(0.dp),
            bottomStart = CornerSize(0.dp)
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = searchValue(),
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(
                    text = stringResource(R.string.core_ui_search),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                focusedPlaceholderColor = Base5,
                unfocusedPlaceholderColor = Base5,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        val items = state()
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = verticalArrangement,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                count = items.size,
                key = { itemKey(items[it]) }
            ) {
                itemComposable(items[it])
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> BottomSheetWithMultipleItems(
    state: () -> BasicUiState<List<T>>,
    onRetryLoad: () -> Unit,
    emptyStateMessage: String,
    itemKey: (T) -> Any,
    itemComposable: @Composable LazyItemScope.(T) -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
) {
    ModalBottomSheet(
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                width = 70.dp,
                shape = MaterialTheme.shapes.medium,
                color = Color(0xFFD9D9D9),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
            )
        },
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.large.copy(
            bottomEnd = CornerSize(0.dp),
            bottomStart = CornerSize(0.dp)
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        val state = state()

        when {
            state.isLoading && !state.isLoaded -> LoadingScreen()
            state.isError && !state.isLoaded -> ErrorScreen(onRetryLoad)
            state.isLoaded -> {
                val items = state.value
                if (items.isEmpty()) {
                    EmptyScreen(
                        message = emptyStateMessage,
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth()
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = verticalArrangement,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            count = items.size,
                            key = { itemKey(items[it]) }
                        ) {
                            itemComposable(items[it])
                        }
                    }
                }
            }
        }
    }
}