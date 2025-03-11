package ru.nativespeakers.feature.auth.ui

import androidx.compose.runtime.compositionLocalOf
import ru.nativespeakers.core.model.AppRole

val LocalAppRoles = compositionLocalOf { emptyList<AppRole>() }