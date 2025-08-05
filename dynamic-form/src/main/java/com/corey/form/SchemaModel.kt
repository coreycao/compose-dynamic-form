package com.corey.form

/**
 * @author Yeung
 * @date 2025/8/5
 */

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FormSchema(
    val type: String, // 固定为 "form"
    val title: String,
    val fields: List<FieldSchema>
)

@Serializable
sealed class FieldSchema {
    abstract val type: String
    abstract val label: String
    abstract val name: String

    @Serializable
    @SerialName("text")
    data class TextField(
        override val type: String = "text",
        override val label: String,
        override val name: String,
        val validation: ValidationRule? = null
    ) : FieldSchema()

    @Serializable
    @SerialName("textarea")
    data class TextAreaField(
        override val type: String = "textarea",
        override val label: String,
        override val name: String,
        val validation: ValidationRule? = null,
        val maxLines: Int = 5
    ) : FieldSchema()

    @Serializable
    @SerialName("number")
    data class NumberField(
        override val type: String = "number",
        override val label: String,
        override val name: String,
        val validation: ValidationRule? = null
    ) : FieldSchema()

    @Serializable
    @SerialName("select")
    data class SelectField(
        override val type: String = "select",
        override val label: String,
        override val name: String,
        val options: List<String>,
        val validation: ValidationRule? = null
    ) : FieldSchema()

    @Serializable
    @SerialName("checkbox")
    data class CheckboxField(
        override val type: String = "checkbox",
        override val label: String,
        override val name: String,
        val checked : Boolean = false,
        val validation: ValidationRule? = null
    ) : FieldSchema()

    @Serializable
    @SerialName("button")
    data class ButtonField(
        override val type: String = "button",
        override val name: String,
        override val label: String,
        val action: String,
    ) : FieldSchema()
}

@Serializable
data class ValidationRule(
    val required: Boolean = false,
    val requiredChecked: Boolean = false,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val regex: String? = null,
    val message: String? = null
)

fun FieldSchema.ButtonField.btnAction(): ButtonAction {
    return when (action) {
        "submit" -> ButtonAction.Submit
        "reset" -> ButtonAction.Reset
        else -> ButtonAction.Custom(action)
    }
}

sealed class ButtonAction {
    data object Submit : ButtonAction()
    data object Reset : ButtonAction()
    data class Custom(val command: String) : ButtonAction()
}

