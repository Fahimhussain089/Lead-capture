package com.assignment.dynamicform.lead

import com.assignment.dynamicform.designsystem.form.CHECKED_VALUE
import com.assignment.dynamicform.designsystem.form.ControlType
import com.assignment.dynamicform.designsystem.form.FieldOption
import com.assignment.dynamicform.designsystem.form.FieldSpan
import com.assignment.dynamicform.designsystem.form.FormFieldSpec
import com.assignment.dynamicform.designsystem.form.TextInput
import com.assignment.dynamicform.designsystem.form.UNCHECKED_VALUE

object LeadFields {
    const val FULL_NAME = "fullName"
    const val EMAIL = "email"
    const val LEAD_TYPE = "leadType"
    const val COMPANY_NAME = "companyName"
    const val PHONE = "phone"
    const val NOTES = "notes"
    const val CONSENT = "consent"

    const val INDIVIDUAL = "individual"
    const val COMPANY = "company"
}

object LeadFormConfig {
    val fields: List<LeadField> = listOf(
        LeadField(
            spec = FormFieldSpec(
                name = LeadFields.FULL_NAME,
                type = ControlType.Text,
                label = "Full name",
                span = FieldSpan.Half,
            ),
            rules = listOf(LeadRule.Required),
        ),
        LeadField(
            spec = FormFieldSpec(
                name = LeadFields.EMAIL,
                type = ControlType.Email,
                label = "Email",
                span = FieldSpan.Half,
            ),
            rules = listOf(LeadRule.Required, LeadRule.Email),
        ),
        LeadField(
            spec = FormFieldSpec(
                name = LeadFields.LEAD_TYPE,
                type = ControlType.Select,
                label = "Lead type",
                placeholder = "Select",
                options = listOf(
                    FieldOption(LeadFields.INDIVIDUAL, "Individual"),
                    FieldOption(LeadFields.COMPANY, "Company"),
                ),
                span = FieldSpan.Full,
            ),
            rules = listOf(LeadRule.Required),
        ),
        LeadField(
            spec = FormFieldSpec(
                name = LeadFields.COMPANY_NAME,
                type = ControlType.Text,
                label = "Company name",
                span = FieldSpan.Full,
            ),
            rules = listOf(LeadRule.Required),
            visibility = Visibility.Equals(LeadFields.LEAD_TYPE, LeadFields.COMPANY),
        ),
        LeadField(
            spec = FormFieldSpec(
                name = LeadFields.PHONE,
                type = ControlType.Text,
                label = "Phone",
                hint = "10 digits",
                textInput = TextInput.Number,
                span = FieldSpan.Full,
            ),
            rules = listOf(LeadRule.Required, LeadRule.Digits(10)),
        ),
        LeadField(
            spec = FormFieldSpec(
                name = LeadFields.NOTES,
                type = ControlType.TextArea,
                label = "Notes",
                span = FieldSpan.Full,
            ),
            rules = listOf(LeadRule.MaxLength(200)),
        ),
        LeadField(
            spec = FormFieldSpec(
                name = LeadFields.CONSENT,
                type = ControlType.Checkbox,
                label = "Consent",
                hint = "I agree to be contacted about this lead.",
                span = FieldSpan.Full,
            ),
            rules = listOf(LeadRule.Required),
        ),
    )

    val byName: Map<String, LeadField> = fields.associateBy { it.name }

    fun emptyValues(): Map<String, String> = fields.associate { field ->
        field.name to if (field.spec.type == ControlType.Checkbox) UNCHECKED_VALUE else ""
    }

    fun visibleFields(values: Map<String, String>): List<LeadField> =
        fields.filter { it.visibility.matches(values) }

    fun isChecked(value: String?): Boolean = value == CHECKED_VALUE
}
