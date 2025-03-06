package ru.nativespeakers.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.conditional(
    condition: Boolean,
    block: @Composable Modifier.() -> Modifier
): Modifier = if (condition) {
    this.block()
} else this

@Composable
fun Modifier.conditional(
    condition: Boolean,
    trueBlock: @Composable Modifier.() -> Modifier,
    falseBlock: @Composable Modifier.() -> Modifier
): Modifier = if (condition) {
    this.trueBlock()
} else this.falseBlock()