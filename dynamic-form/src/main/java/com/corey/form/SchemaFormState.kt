package com.corey.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * @author Yeung
 * @date 2025/8/5
 */

@Composable
fun rememberSchemaFormState(): FormState {
    return remember {
        FormState()
    }
}

class FormState() {

    private val controllers = mutableMapOf<String, FieldController<*>>()

    @Suppress("UNCHECKED_CAST")
    internal fun <T> bind(fieldSchema: FieldSchema, default: T): FieldController<T> {
        val name = fieldSchema.name
        val existing = controllers[name]
        if (existing != null) return existing as FieldController<T>

        val controller = FieldController(
            filedSchema = fieldSchema,
            default = default,
            field = mutableStateOf(default),
            error = mutableStateOf(null)
        )
        controllers[name] = controller
        return controller
    }

    fun validate(): Boolean {
        var allValid = true
        controllers.values.forEach { controller ->
            val isValid = controller.validate()
            if (!isValid) allValid = false
        }
        return allValid
    }

    fun resetAllFields() {
        controllers.forEach { (name, controller) ->
            controller.reset()
        }
    }

    fun getTypedAllValues(): Map<String, Any?> {
        return controllers.mapValues { it.value.field.value }
    }

    fun getAllValues(): Map<String, String> {
        return controllers.mapValues { it.value.field.value?.toString() ?: "" }
    }
}

class FieldController<T>(
    private val filedSchema: FieldSchema,
    val default: T,
    val field: MutableState<T>,
    val error: MutableState<String?>,
) {
    internal fun reset() {
        field.value = default
        error.value = null
    }

    internal fun validate(): Boolean {
        val rule = filedSchema.validation

        if (rule == null) {
            error.value = null
            return true
        }

        val v = field.value

        val errorText = when (v) {
            is String -> {
                if (rule.required && v.isBlank()) rule.message ?: "不能为空"
                else if (rule.minLength != null && v.length < rule.minLength) "最少 ${rule.minLength} 个字符"
                else if (rule.maxLength != null && v.length > rule.maxLength) "最多 ${rule.maxLength} 个字符"
                else if (rule.regex != null && !Regex(rule.regex).matches(v)) "格式不符合要求"
                else null
            }

            is Boolean -> {
                if (rule.requiredChecked && !v) rule.message ?: "必须勾选"
                else null
            }

            else -> null
        }

        error.value = errorText
        return errorText == null
    }
}
