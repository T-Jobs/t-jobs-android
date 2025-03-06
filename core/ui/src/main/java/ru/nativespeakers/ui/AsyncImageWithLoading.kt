package ru.nativespeakers.ui

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.valentinilk.shimmer.shimmer

@Composable
fun AsyncImageWithLoading(
    model: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    var shouldShowShimmer by rememberSaveable { mutableStateOf(true) }
    val shimmerColor = Color.Gray.copy(alpha = 0.5f)

    AsyncImage(
        model = model,
        contentScale = contentScale,
        contentDescription = "image",
        onSuccess = { shouldShowShimmer = false },
        modifier = modifier
            .conditional(shouldShowShimmer) {
                background(shimmerColor)
                shimmer()
            }
    )
}