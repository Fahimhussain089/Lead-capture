package com.assignment.dynamicform.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.assignment.dynamicform.designsystem.token.ColorTokens
import com.assignment.dynamicform.designsystem.token.TypeTokens

private val Colors = lightColorScheme(
    primary = ColorTokens.accent,
    onPrimary = ColorTokens.onAccent,
    background = ColorTokens.background,
    onBackground = ColorTokens.ink,
    surface = ColorTokens.surface,
    onSurface = ColorTokens.ink,
    error = ColorTokens.error,
    onError = ColorTokens.onAccent,
)

private val Typography = Typography(
    headlineMedium = TypeTokens.title,
    bodyLarge = TypeTokens.body,
    bodyMedium = TypeTokens.supporting,
    labelLarge = TypeTokens.label,
    labelMedium = TypeTokens.hint,
)

@Composable
fun DesignTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Colors,
        typography = Typography,
        content = content,
    )
}
