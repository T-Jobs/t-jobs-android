package ru.nativespeakers.feature.auth.ui

import androidx.compose.runtime.compositionLocalOf
import ru.nativespeakers.data.auth.AppRole

val LocalAppRoles = compositionLocalOf { emptyList<AppRole>() }