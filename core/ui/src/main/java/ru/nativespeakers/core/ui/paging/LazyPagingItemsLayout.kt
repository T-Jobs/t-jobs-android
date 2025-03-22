package ru.nativespeakers.core.ui.paging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import ru.nativespeakers.core.ui.screen.LoadingScreen

@Composable
fun <T : Any> LazyPagingItemsColumn(
    items: LazyPagingItems<T>,
    isPullToRefreshActive: Boolean,
    itemKey: ((item: @JvmSuppressWildcards T) -> Any)?,
    itemsEmptyComposable: @Composable () -> Unit,
    itemComposable: @Composable (T) -> Unit,
    errorWhileRefreshComposable: @Composable () -> Unit,
    errorWhileAppendComposable: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    refreshIndicatorStrokeWidth: Dp = 3.dp,
    refreshIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    refreshIndicatorSize: Dp = 40.dp,
    appendIndicatorStrokeWidth: Dp = 3.dp,
    appendIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    appendIndicatorSize: Dp = 24.dp
) {
    when {
        items.isErrorWhileRefreshing() -> errorWhileRefreshComposable()
        items.isRefreshing() && !isPullToRefreshActive -> {
            LoadingScreen(
                indicatorColor = refreshIndicatorColor,
                indicatorStrokeWidth = refreshIndicatorStrokeWidth,
                indicatorSize = refreshIndicatorSize,
                modifier = Modifier.padding(20.dp)
            )
        }

        items.isEmpty() -> itemsEmptyComposable()
        else -> {
            LazyColumn(
                state = lazyListState,
                contentPadding = contentPadding,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                modifier = modifier
            ) {
                items(
                    count = items.itemCount,
                    key = items.itemKey(key = itemKey)
                ) {
                    val item = items[it]!!
                    itemComposable(item)
                }
                if (items.isAppending()) {
                    item {
                        LoadingScreen(
                            indicatorStrokeWidth = appendIndicatorStrokeWidth,
                            indicatorColor = appendIndicatorColor,
                            indicatorSize = appendIndicatorSize,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
                if (items.isErrorWhileAppending()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        ) {
                            errorWhileAppendComposable()
                        }
                    }
                }
            }
        }
    }
}