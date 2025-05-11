package ru.nativespeakers.core.ui.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.withResumed

@Composable
fun ResumedEventExecutor(
    vararg keys: Any?,
    block: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(*keys, lifecycleOwner) {
        lifecycleOwner.withResumed(block)
    }
}