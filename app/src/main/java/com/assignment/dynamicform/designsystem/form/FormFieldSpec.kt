package com.assignment.dynamicform.designsystem.form

/**
 * Generic control description. It knows field types, not what a lead is.
 * Adding a control to a form means adding one of these.
 */
data class FormFieldSpec(
    val name: String,
    val type: ControlType,
    val label: String,
    val hint: String? = null,
    val placeholder: String? = null,
    val options: List<FieldOption> = emptyList(),
    val span: FieldSpan = FieldSpan.Full,
    val textInput: TextInput = TextInput.Plain,
)

enum class ControlType {
    Text,
    Email,
    Select,
    TextArea,
    Checkbox,
}

enum class FieldSpan {
    /** Shares a row with a neighboring half field when the layout is expanded. */
    Half,

    /** Always occupies the full content width. */
    Full,
}

enum class TextInput {
    Plain,
    Number,
}

data class FieldOption(val value: String, val label: String)

const val CHECKED_VALUE = "true"
const val UNCHECKED_VALUE = "false"
