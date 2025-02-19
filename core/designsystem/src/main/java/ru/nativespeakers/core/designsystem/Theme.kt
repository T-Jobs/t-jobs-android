package ru.nativespeakers.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

internal val LightColorScheme = lightColorScheme(
    primary = Primary5,
    onPrimary = Base0,
    primaryContainer = Primary2,
    onPrimaryContainer = Primary8,
    background = Base0,
    surface = Base0,
    onSurface = Base10,
    onSurfaceVariant = Base4,
    outline = Base4,
    outlineVariant = Base2,
    error = Red5,
    onError = Base0,
    errorContainer = Red2,
    onErrorContainer = Red8,
)

@Composable
fun TJobTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        shapes = Shapes,
        typography = Typography,
        content = content
    )
}