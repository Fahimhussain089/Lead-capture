package com.assignment.dynamicform.designsystem.atom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.assignment.dynamicform.designsystem.form.FieldOption
import com.assignment.dynamicform.designsystem.token.ColorTokens
import com.assignment.dynamicform.designsystem.token.TypeTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DsSelect(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<FieldOption>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isError: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.value == value }?.label.orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { next ->
            if (expanded && !next) onDismiss()
            expanded = next
        },
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .semantics { contentDescription = label },
            readOnly = true,
            textStyle = TypeTokens.body.copy(color = ColorTokens.ink),
            placeholder = placeholder?.let { text ->
                {
                    Text(
                        text = text,
                        style = TypeTokens.body,
                        color = ColorTokens.inkMuted,
                    )
                }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            singleLine = true,
            shape = AtomFieldShape,
            colors = atomTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                onDismiss()
            },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            style = TypeTokens.body,
                            color = ColorTokens.ink,
                        )
                    },
                    onClick = {
                        onValueChange(option.value)
                        expanded = false
                        onDismiss()
                    },
                )
            }
        }
    }
}
