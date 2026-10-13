package com.assignment.dynamicform.lead

import com.assignment.dynamicform.designsystem.form.FormFieldSpec

/**
 * One config entry: the control to render, the rules to enforce, and when it is on screen.
 */
data class LeadField(
    val spec: FormFieldSpec,
    val rules: List<LeadRule> = emptyList(),
    val visibility: Visibility = Visibility.Always,
) {
    val name: String get() = spec.name
}

sealed interface LeadRule {
    data object Required : LeadRule
    data object Email : LeadRule
    data class Digits(val count: Int) : LeadRule
    data class MaxLength(val max: Int) : LeadRule
}

sealed interface Visibility {
    data object Always : Visibility
    data class Equals(val field: String, val value: String) : Visibility

    fun matches(values: Map<String, String>): Boolean = when (this) {
        Always -> true
        is Equals -> values[field] == value
    }
}
