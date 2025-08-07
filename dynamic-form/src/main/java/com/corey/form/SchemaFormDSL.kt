package com.corey.form

/**
 * @author Yeung
 * @date 2025/8/6
 */

fun formSchema(
    title: String,
    block: FormSchemaBuilder.() -> Unit
): FormSchema {
    return FormSchemaBuilder(title).apply(block).build()
}

@DslMarker
annotation class FormDsl

@FormDsl
class FormSchemaBuilder(
    private val title: String
) {
    var description: String? = null
    var cover: String? = null

    private val fields = mutableListOf<FieldSchema>()

    fun text(label: String, name: String, block: (ValidationBuilder.() -> Unit)? = null) {
        fields += FieldSchema.TextField(label = label, name = name, validation = block?.toValidationRule())
    }

    fun textarea(label: String, name: String, maxLines: Int = 5, block: (ValidationBuilder.() -> Unit)? = null) {
        fields += FieldSchema.TextAreaField(label = label, name = name, maxLines = maxLines, validation = block?.toValidationRule())
    }

    fun number(label: String, name: String, block: (ValidationBuilder.() -> Unit)? = null) {
        fields += FieldSchema.NumberField(label = label, name = name, validation = block?.toValidationRule())
    }

    fun select(label: String, name: String, options: List<String> = emptyList(), block: (ValidationBuilder.() -> Unit)? = null) {
        fields += FieldSchema.SelectField(label = label, name = name, options = options, validation = block?.toValidationRule())
    }

    fun checkbox(label: String, name: String, checked: Boolean = false, block: (ValidationBuilder.() -> Unit)? = null) {
        fields += FieldSchema.CheckboxField(label = label, name = name, checked = checked, validation = block?.toValidationRule())
    }

    fun date(label: String, name: String, block: (ValidationBuilder.() -> Unit)? = null) {
        fields += FieldSchema.DatePickerField(label = label, name = name, validation = block?.toValidationRule())
    }

    fun button(label: String, name: String, action: String = "submit", block: (ValidationBuilder.() -> Unit)? = null) {
        fields += FieldSchema.ButtonField(label = label, name = name, action = action, validation = block?.toValidationRule())
    }

    fun build(): FormSchema {
        return FormSchema(
            type = "form",
            title = title,
            description = description,
            cover = cover,
            fields = fields
        )
    }
}

@FormDsl
class ValidationBuilder {
    var required: Boolean = false
    var requiredChecked: Boolean = false
    var minLength: Int? = null
    var maxLength: Int? = null
    var regex: String? = null
    var message: String? = null

    fun build(): ValidationRule = ValidationRule(
        required = required,
        requiredChecked = requiredChecked,
        minLength = minLength,
        maxLength = maxLength,
        regex = regex,
        message = message
    )
}

fun (ValidationBuilder.() -> Unit).toValidationRule(): ValidationRule {
    return ValidationBuilder().apply(this).build()
}



