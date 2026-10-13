package com.assignment.dynamicform.designsystem.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.assignment.dynamicform.designsystem.token.LayoutTokens
import com.assignment.dynamicform.designsystem.token.SpacingTokens

/** True when [maxWidth] is at or above [LayoutTokens.expandedMinWidth]. */
internal fun isExpandedWidth(maxWidth: Dp): Boolean =
    maxWidth >= LayoutTokens.expandedMinWidth

/**
 * Packs half-span fields into pairs. A half field with no neighbor stays alone.
 * Full-span fields always occupy a row by themselves.
 */
internal fun fieldRows(fields: List<FormFieldSpec>): List<List<FormFieldSpec>> {
    val rows = mutableListOf<List<FormFieldSpec>>()
    var index = 0
    while (index < fields.size) {
        val current = fields[index]
        val next = fields.getOrNull(index + 1)
        if (current.span == FieldSpan.Half && next?.span == FieldSpan.Half) {
            rows += listOf(current, next)
            index += 2
        } else {
            rows += listOf(current)
            index += 1
        }
    }
    return rows
}

/**
 * Compact: one column. Expanded: two columns, using [fieldRows].
 * This file owns the compact and expanded layouts.
 */
@Composable
internal fun FormColumns(
    fields: List<FormFieldSpec>,
    modifier: Modifier = Modifier,
    field: @Composable (FormFieldSpec) -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (!isExpandedWidth(maxWidth)) {
            Column(verticalArrangement = Arrangement.spacedBy(SpacingTokens.fieldGap)) {
                fields.forEach { spec ->
                    key(spec.name) { field(spec) }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(SpacingTokens.fieldGap)) {
                fieldRows(fields).forEach { row ->
                    if (row.size == 1 && row.first().span == FieldSpan.Full) {
                        key(row.first().name) { field(row.first()) }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.md),
                        ) {
                            row.forEach { spec ->
                                key(spec.name) {
                                    Box(modifier = Modifier.weight(1f)) { field(spec) }
                                }
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
