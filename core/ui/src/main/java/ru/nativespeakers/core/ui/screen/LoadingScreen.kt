package ru.nativespeakers.core.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    indicatorStrokeWidth: Dp = 3.dp,
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    indicatorSize: Dp = 30.dp
) {
    CircularProgressIndicator(
        color = indicatorColor,
        strokeWidth = indicatorStrokeWidth,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .requiredSize(indicatorSize)
    )
}