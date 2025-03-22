package ru.nativespeakers.core.ui

import androidx.compose.runtime.Immutable

@Immutable
data class BasicUiState<T>(
    val value: T,
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val isError: Boolean = false,
)