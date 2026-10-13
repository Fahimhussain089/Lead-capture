package com.assignment.dynamicform.designsystem.atom

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.assignment.dynamicform.designsystem.token.ColorTokens

@Composable
fun DsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.semantics { contentDescription = label },
        colors = CheckboxDefaults.colors(
            checkedColor = ColorTokens.accent,
            uncheckedColor = ColorTokens.inkMuted,
            checkmarkColor = ColorTokens.onAccent,
        ),
    )
}
