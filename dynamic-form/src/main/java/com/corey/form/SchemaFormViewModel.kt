package com.corey.form

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author Yeung
 * @date 2025/8/5
 *
 */
class SchemaFormViewModel() : ViewModel() {

    sealed class SubmitState {
        object Idle : SubmitState()
        object Success : SubmitState()
        object Error : SubmitState()
    }

    val formState = FormState()

    val submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)

    fun performAction(btnFieldSchema: FieldSchema.ButtonField,
                      onSubmit: (Map<String, Any?>) -> Unit,
                      onCustomAction: ((String) -> Unit)? = null) {

        val action = btnFieldSchema.btnAction()
        when (action) {
            is ButtonAction.Submit -> {
                if (validate()) {
                    Log.d("performAction", "Submit form: ${formState.getAllValues()}")
                    onSubmit(formState.getTypedAllValues())
                }
            }
            is ButtonAction.Reset -> resetForm()
            is ButtonAction.Custom -> {
                Log.d("performAction", "action command: ${action.command}")
                onCustomAction?.invoke(action.command)
            }
        }
    }

    private fun resetForm() {
        formState.resetAllFields()
        submitState.value = SubmitState.Idle
    }

    fun validate(): Boolean {
        return formState.validate().also { valid ->
            submitState.value = if (valid) SubmitState.Success else SubmitState.Error
        }
    }
}
