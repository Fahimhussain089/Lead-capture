package com.assignment.dynamicform.lead

import com.assignment.dynamicform.designsystem.form.CHECKED_VALUE
import com.assignment.dynamicform.designsystem.form.ControlType

private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

/**
 * Pure lead rules. One message per visible field that fails, in config order.
 * Hidden fields are skipped, so company name is required only while it is visible.
 * The first failing rule on a field wins.
 */
fun validateLeadForm(
    fields: List<LeadField>,
    values: Map<String, String>,
): Map<String, String> {
    val errors = linkedMapOf<String, String>()
    for (field in fields) {
        if (!field.visibility.matches(values)) continue
        val value = values[field.name].orEmpty()
        for (rule in field.rules) {
            if (!rule.passes(field, value)) {
                errors[field.name] = rule.message(field)
                break
            }
        }
    }
    return errors
}

/** Keeps typed input inside the same rules the validator enforces. */
fun sanitizeLeadValue(field: LeadField, raw: String): String {
    var value = raw
    field.rules.filterIsInstance<LeadRule.Digits>().firstOrNull()?.let { rule ->
        value = value.filter { it.isDigit() }.take(rule.count)
    }
    field.rules.filterIsInstance<LeadRule.MaxLength>().firstOrNull()?.let { rule ->
        value = value.take(rule.max)
    }
    return value
}

private fun LeadRule.passes(field: LeadField, value: String): Boolean = when (this) {
    LeadRule.Required -> when (field.spec.type) {
        ControlType.Checkbox -> value == CHECKED_VALUE
        ControlType.Select -> field.spec.options.any { it.value == value }
        else -> value.trim().isNotEmpty()
    }
    LeadRule.Email -> EMAIL.matches(value.trim())
    is LeadRule.Digits -> value.length == count && value.all { it.isDigit() }
    is LeadRule.MaxLength -> value.length <= max
}

private fun LeadRule.message(field: LeadField): String {
    val label = field.spec.label.replaceFirstChar { it.lowercase() }
    return when (this) {
        LeadRule.Required -> when (field.spec.type) {
            ControlType.Select -> "Select $label."
            ControlType.Checkbox -> "${field.spec.label} is required."
            else -> "Enter $label."
        }
        LeadRule.Email -> "Enter a valid email address."
        is LeadRule.Digits -> "Enter a $count-digit number."
        is LeadRule.MaxLength -> "${field.spec.label} must be $max characters or fewer."
    }
}
