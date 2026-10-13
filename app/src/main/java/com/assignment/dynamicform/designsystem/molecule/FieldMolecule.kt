package com.assignment.dynamicform.designsystem.molecule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.assignment.dynamicform.designsystem.token.ColorTokens
import com.assignment.dynamicform.designsystem.token.SpacingTokens
import com.assignment.dynamicform.designsystem.token.TypeTokens

/**
 * Label, control, hint, and error for one field.
 * The control is supplied by the caller so this molecule stays free of field rules.
 */
@Composable
fun FieldMolecule(
    label: String,
    hint: String?,
    error: String?,
    modifier: Modifier = Modifier,
    labelBesideControl: Boolean = false,
    onLabelClick: (() -> Unit)? = null,
    control: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.xs),
    ) {
        if (labelBesideControl) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                control()
                Text(
                    text = label,
                    style = TypeTokens.label,
                    color = ColorTokens.ink,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = SpacingTokens.minTouch)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .then(
                            if (onLabelClick != null) {
                                Modifier.clickable(onClick = onLabelClick)
                            } else {
                                Modifier
                            },
                        )
                        .clearAndSetSemantics {},
                )
            }
        } else {
            Text(
                text = label,
                style = TypeTokens.label,
                color = ColorTokens.ink,
                modifier = Modifier.clearAndSetSemantics {},
            )
            control()
        }
        if (!hint.isNullOrBlank()) {
            Text(
                text = hint,
                style = TypeTokens.hint,
                color = ColorTokens.inkMuted,
            )
        }
        if (!error.isNullOrBlank()) {
            Text(
                text = error,
                style = TypeTokens.error,
                color = ColorTokens.error,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
    }
}
