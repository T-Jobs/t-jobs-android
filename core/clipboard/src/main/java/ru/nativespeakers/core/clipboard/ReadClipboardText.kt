package ru.nativespeakers.core.clipboard

import android.content.ClipData
import androidx.compose.ui.platform.Clipboard

fun Clipboard.getClipboardText(): String? {
    return nativeClipboard.primaryClip?.toString()
}

fun Clipboard.setClipboardText(text: String?) {
    val clipData = ClipData.newPlainText(text, text)
    nativeClipboard.setPrimaryClip(clipData)
}