package com.assignment.dynamicform.designsystem.atom

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import com.assignment.dynamicform.designsystem.token.ColorTokens
import com.assignment.dynamicform.designsystem.token.SpacingTokens

internal val AtomFieldShape: Shape = RoundedCornerShape(SpacingTokens.radiusMd)

@Composable
internal fun atomTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = ColorTokens.ink,
    unfocusedTextColor = ColorTokens.ink,
    disabledTextColor = ColorTokens.inkMuted,
    errorTextColor = ColorTokens.ink,
    focusedContainerColor = ColorTokens.surface,
    unfocusedContainerColor = ColorTokens.surface,
    disabledContainerColor = ColorTokens.surface,
    errorContainerColor = ColorTokens.surface,
    cursorColor = ColorTokens.accent,
    errorCursorColor = ColorTokens.error,
    focusedBorderColor = ColorTokens.accent,
    unfocusedBorderColor = ColorTokens.border,
    disabledBorderColor = ColorTokens.border,
    errorBorderColor = ColorTokens.error,
    focusedPlaceholderColor = ColorTokens.inkMuted,
    unfocusedPlaceholderColor = ColorTokens.inkMuted,
    disabledPlaceholderColor = ColorTokens.inkMuted,
    errorPlaceholderColor = ColorTokens.inkMuted,
)
