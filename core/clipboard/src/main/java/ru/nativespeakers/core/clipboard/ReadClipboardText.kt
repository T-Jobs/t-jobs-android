package ru.nativespeakers.core.clipboard

import androidx.compose.ui.platform.ClipboardManager

fun getClipboardText(manager: ClipboardManager): String? {
    return manager.getText()?.text
}