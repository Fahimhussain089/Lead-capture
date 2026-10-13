@file:OptIn(ExperimentalFoundationApi::class)

package com.assignment.dynamicform.lead

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.assignment.dynamicform.designsystem.atom.DsButton
import com.assignment.dynamicform.designsystem.form.CHECKED_VALUE
import com.assignment.dynamicform.designsystem.form.ControlType
import com.assignment.dynamicform.designsystem.form.DynamicForm
import com.assignment.dynamicform.designsystem.form.FormFieldSpec
import com.assignment.dynamicform.designsystem.theme.DesignTheme
import com.assignment.dynamicform.designsystem.token.ColorTokens
import com.assignment.dynamicform.designsystem.token.LayoutTokens
import com.assignment.dynamicform.designsystem.token.SpacingTokens
import com.assignment.dynamicform.designsystem.token.TypeTokens

private val stringMapSaver = mapSaver(
    save = { value ->
        buildMap<String, Any> {
            value.forEach { (key, entry) -> put(key, entry) }
        }
    },
    restore = { saved -> saved.mapValues { it.value as String } },
)

private val stringSetSaver = listSaver<Set<String>, String>(
    save = { it.toList() },
    restore = { it.toSet() },
)

private data class SubmittedLine(val label: String, val value: String)

private val submittedLinesSaver = listSaver<List<SubmittedLine>, String>(
    save = { lines -> lines.flatMap { listOf(it.label, it.value) } },
    restore = { stored ->
        stored.chunked(2).map { pair -> SubmittedLine(pair[0], pair[1]) }
    },
)

@Composable
fun LeadFormScreen() {
    val focusManager = LocalFocusManager.current
    val scroll = rememberScrollState()
    var values by rememberSaveable(stateSaver = stringMapSaver) {
        mutableStateOf(LeadFormConfig.emptyValues())
    }
    var touched by rememberSaveable(stateSaver = stringSetSaver) {
        mutableStateOf(emptySet<String>())
    }
    var submitAttempted by rememberSaveable { mutableStateOf(false) }
    var submission by rememberSaveable(stateSaver = submittedLinesSaver) {
        mutableStateOf(emptyList<SubmittedLine>())
    }

    val errors = validateLeadForm(LeadFormConfig.fields, values)
    val visibleErrors = if (submitAttempted) {
        errors
    } else {
        errors.filterKeys { it in touched }
    }
    val specs = LeadFormConfig.visibleFields(values).map { it.spec.withLiveHint(it, values) }

    val onValueChange = { name: String, raw: String ->
        val field = LeadFormConfig.byName.getValue(name)
        val next = sanitizeLeadValue(field, raw)
        if (next != values[name]) {
            values = values + (name to next)
            submission = emptyList()
        }
    }
    val onFocusLost = { name: String ->
        touched = touched + name
    }
    val onSubmit = {
        focusManager.clearFocus(force = true)
        submitAttempted = true
        val current = validateLeadForm(LeadFormConfig.fields, values)
        submission = if (current.isEmpty()) displayLines(values) else emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorTokens.background)
            .windowInsetsPadding(
                WindowInsets.systemBars
                    .union(WindowInsets.displayCutout)
                    .union(WindowInsets.ime),
            ),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = LayoutTokens.formMaxWidth)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(scroll)
                    .padding(horizontal = SpacingTokens.screenHorizontal)
                    .padding(top = SpacingTokens.lg, bottom = SpacingTokens.md),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.lg),
            ) {
                Header()
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SpacingTokens.radiusLg),
                    color = ColorTokens.surface,
                    border = BorderStroke(SpacingTokens.hairline, ColorTokens.border),
                ) {
                    DynamicForm(
                        fields = specs,
                        values = values,
                        errors = visibleErrors,
                        onValueChange = onValueChange,
                        onFocusLost = onFocusLost,
                        modifier = Modifier.padding(SpacingTokens.md),
                    )
                }
                if (submission.isNotEmpty()) {
                    SubmittedLead(lines = submission)
                }
            }
        }
        SubmitBar(onClick = onSubmit)
    }
}

@Composable
private fun Header() {
    Column(verticalArrangement = Arrangement.spacedBy(SpacingTokens.sm)) {
        Text(
            text = "LEAD",
            style = TypeTokens.eyebrow,
            color = ColorTokens.accent,
        )
        Text(
            text = "Lead capture",
            style = TypeTokens.title,
            color = ColorTokens.ink,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = "Tell us who the lead is. Company details appear when the lead is a company.",
            style = TypeTokens.supporting,
            color = ColorTokens.inkMuted,
        )
    }
}

@Composable
private fun SubmitBar(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorTokens.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(SpacingTokens.hairline)
                .background(ColorTokens.border),
        )
        DsButton(
            label = "Submit",
            onClick = onClick,
            modifier = Modifier
                .widthIn(max = LayoutTokens.formMaxWidth)
                .padding(
                    PaddingValues(
                        start = SpacingTokens.screenHorizontal,
                        end = SpacingTokens.screenHorizontal,
                        top = SpacingTokens.sm,
                        bottom = SpacingTokens.md,
                    ),
                ),
        )
    }
}

@Composable
private fun SubmittedLead(lines: List<SubmittedLine>) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    LaunchedEffect(lines) { bringIntoViewRequester.bringIntoView() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .background(ColorTokens.successSurface, RoundedCornerShape(SpacingTokens.radiusLg))
            .padding(SpacingTokens.md),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.sm),
    ) {
        Text(
            text = "Submitted lead",
            style = TypeTokens.summaryTitle,
            color = ColorTokens.successInk,
        )
        lines.forEach { line ->
            Column(verticalArrangement = Arrangement.spacedBy(SpacingTokens.xs)) {
                Text(
                    text = line.label,
                    style = TypeTokens.hint,
                    color = ColorTokens.successInk,
                )
                Text(
                    text = line.value,
                    style = TypeTokens.body,
                    color = ColorTokens.ink,
                )
            }
        }
    }
}

private fun FormFieldSpec.withLiveHint(field: LeadField, values: Map<String, String>): FormFieldSpec {
    val max = field.rules.filterIsInstance<LeadRule.MaxLength>().firstOrNull() ?: return this
    if (type != ControlType.TextArea) return this
    val count = values[field.name].orEmpty().length
    return copy(hint = "Optional · $count/${max.max}")
}

private fun displayLines(values: Map<String, String>): List<SubmittedLine> {
    return LeadFormConfig.visibleFields(values).map { field ->
        val raw = values[field.name].orEmpty()
        val shown = when (field.spec.type) {
            ControlType.Checkbox -> if (raw == CHECKED_VALUE) "Yes" else "No"
            ControlType.Select -> field.spec.options.firstOrNull { it.value == raw }?.label ?: raw
            else -> raw.trim().ifBlank { "—" }
        }
        SubmittedLine(field.spec.label, shown)
    }
}

@Preview(name = "Compact", widthDp = 390, heightDp = 844)
@Preview(name = "Expanded", widthDp = 840, heightDp = 900)
@Composable
private fun LeadFormScreenPreview() {
    DesignTheme {
        LeadFormScreen()
    }
}
