package ru.nativespeakers.core.clipboard

import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString

fun ClipboardManager.getClipboardText(): String? {
    return getText()?.text
}

fun ClipboardManager.setClipboardText(text: String?) {
    text?.let { setText(AnnotatedString(it)) }
}