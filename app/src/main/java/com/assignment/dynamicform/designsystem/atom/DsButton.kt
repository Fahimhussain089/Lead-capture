package com.assignment.dynamicform.designsystem.atom

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.assignment.dynamicform.designsystem.token.ColorTokens
import com.assignment.dynamicform.designsystem.token.SpacingTokens
import com.assignment.dynamicform.designsystem.token.TypeTokens

@Composable
fun DsButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = SpacingTokens.buttonHeight),
        shape = RoundedCornerShape(SpacingTokens.radiusMd),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorTokens.accent,
            contentColor = ColorTokens.onAccent,
            disabledContainerColor = ColorTokens.accent.copy(alpha = 0.38f),
            disabledContentColor = ColorTokens.onAccent,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
        ),
        contentPadding = PaddingValues(
            horizontal = SpacingTokens.lg,
            vertical = SpacingTokens.md,
        ),
    ) {
        Text(text = label, style = TypeTokens.button)
    }
}
