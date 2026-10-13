@file:OptIn(ExperimentalFoundationApi::class)

package com.assignment.dynamicform.designsystem.form

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.assignment.dynamicform.designsystem.atom.DsCheckbox
import com.assignment.dynamicform.designsystem.atom.DsSelect
import com.assignment.dynamicform.designsystem.atom.DsTextField
import com.assignment.dynamicform.designsystem.molecule.FieldMolecule

/**
 * Renders one field per config entry and maps [ControlType] to an atom.
 * Values, errors, and rules stay with the caller.
 */
@Composable
fun DynamicForm(
    fields: List<FormFieldSpec>,
    values: Map<String, String>,
    errors: Map<String, String>,
    onValueChange: (name: String, value: String) -> Unit,
    onFocusLost: (name: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val textNames = fields.mapNotNull { field ->
        field.name.takeIf {
            field.type == ControlType.Text ||
                field.type == ControlType.Email ||
                field.type == ControlType.TextArea
        }
    }
    val focusRequesters = remember(textNames) {
        textNames.associateWith { FocusRequester() }
    }
    val bringRequesters = remember(fields.map { it.name }) {
        fields.associate { it.name to BringIntoViewRequester() }
    }
    var focusedName by remember { mutableStateOf<String?>(null) }
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val focusedError = focusedName?.let { errors[it] }

    LaunchedEffect(focusedName, imeBottom, focusedError) {
        val name = focusedName ?: return@LaunchedEffect
        bringRequesters[name]?.bringIntoView()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { state ->
                if (!state.hasFocus) focusedName = null
            },
    ) {
        FormColumns(fields = fields) { field ->
            FormControl(
                field = field,
                value = values[field.name].orEmpty(),
                error = errors[field.name],
                focusModifier = focusRequesters[field.name]?.let { Modifier.focusRequester(it) }
                    ?: Modifier,
                bringModifier = Modifier.bringIntoViewRequester(
                    bringRequesters.getValue(field.name),
                ),
                imeAction = if (textNames.lastOrNull() == field.name) ImeAction.Done else ImeAction.Next,
                onImeAction = {
                    val next = textNames.getOrNull(textNames.indexOf(field.name) + 1)
                    if (next == null) focusManager.clearFocus() else focusRequesters.getValue(next).requestFocus()
                },
                onValueChange = { onValueChange(field.name, it) },
                onFocusLost = { onFocusLost(field.name) },
                onFocused = { focusedName = field.name },
            )
        }
    }
}

@Composable
private fun FormControl(
    field: FormFieldSpec,
    value: String,
    error: String?,
    focusModifier: Modifier,
    bringModifier: Modifier,
    imeAction: ImeAction,
    onImeAction: () -> Unit,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit,
    onFocused: () -> Unit,
) {
    var hadFocus by remember(field.name) { mutableStateOf(false) }
    val modifier = bringModifier.onFocusChanged { state ->
        if (state.hasFocus) {
            hadFocus = true
            onFocused()
        } else if (hadFocus) {
            hadFocus = false
            if (field.type != ControlType.Select) onFocusLost()
        }
    }

    when (field.type) {
        ControlType.Text, ControlType.Email, ControlType.TextArea -> {
            val multiline = field.type == ControlType.TextArea
            FieldMolecule(
                label = field.label,
                hint = field.hint,
                error = error,
                modifier = modifier,
            ) {
                DsTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = field.label,
                    modifier = focusModifier,
                    placeholder = field.placeholder,
                    isError = error != null,
                    singleLine = !multiline,
                    minLines = if (multiline) 4 else 1,
                    keyboardType = keyboardType(field),
                    imeAction = imeAction,
                    capitalization = capitalization(field),
                    onImeAction = onImeAction,
                )
            }
        }

        ControlType.Select -> {
            FieldMolecule(
                label = field.label,
                hint = field.hint,
                error = error,
                modifier = modifier,
            ) {
                DsSelect(
                    value = value,
                    onValueChange = onValueChange,
                    label = field.label,
                    options = field.options,
                    placeholder = field.placeholder,
                    isError = error != null,
                    onDismiss = onFocusLost,
                )
            }
        }

        ControlType.Checkbox -> {
            val checked = value == CHECKED_VALUE
            FieldMolecule(
                label = field.label,
                hint = field.hint,
                error = error,
                labelBesideControl = true,
                onLabelClick = {
                    onValueChange(if (checked) UNCHECKED_VALUE else CHECKED_VALUE)
                    onFocusLost()
                },
                modifier = modifier,
            ) {
                DsCheckbox(
                    checked = checked,
                    onCheckedChange = { next ->
                        onValueChange(if (next) CHECKED_VALUE else UNCHECKED_VALUE)
                        onFocusLost()
                    },
                    label = field.label,
                )
            }
        }
    }
}

private fun keyboardType(field: FormFieldSpec): KeyboardType = when {
    field.type == ControlType.Email -> KeyboardType.Email
    field.textInput == TextInput.Number -> KeyboardType.Number
    else -> KeyboardType.Text
}

private fun capitalization(field: FormFieldSpec): KeyboardCapitalization = when (field.type) {
    ControlType.TextArea -> KeyboardCapitalization.Sentences
    ControlType.Text -> when (field.textInput) {
        TextInput.Number -> KeyboardCapitalization.None
        TextInput.Plain -> KeyboardCapitalization.Words
    }
    else -> KeyboardCapitalization.None
}
