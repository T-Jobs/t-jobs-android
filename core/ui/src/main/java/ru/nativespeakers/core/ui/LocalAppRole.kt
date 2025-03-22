package ru.nativespeakers.core.ui

import androidx.compose.runtime.compositionLocalOf
import ru.nativespeakers.core.model.AppRole

val LocalAppRoles = compositionLocalOf { emptyList<AppRole>() }