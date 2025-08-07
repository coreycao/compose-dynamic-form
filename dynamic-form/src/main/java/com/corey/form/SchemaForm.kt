package com.corey.form

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Date
import java.util.Locale

/**
 * @author Yeung
 * @date 2025/8/5
 */

@Composable
fun SchemaFormScreen(
    modifier: Modifier = Modifier,
    formSchema: FormSchema,
    onSubmit: (formResult: Map<String, Any?>) -> Unit = {},
    onAction: (command: String) -> Unit = {}
) {
    val formViewModel = viewModel<SchemaFormViewModel>()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(text = formSchema.title, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        for (schema in formSchema.fields) {
            Spacer(modifier = Modifier.height(12.dp))
            RenderField(schema, formViewModel.formState) {
                when (schema) {
                    is FieldSchema.ButtonField -> {
                        formViewModel.performAction(schema, onSubmit, onAction)
                    }

                    else -> {
                        Log.w(
                            "SchemaFormScreen",
                            "Unsupported field type for action: ${schema.type}"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenderField(fieldSchema: FieldSchema, formState: FormState, onAction: (() -> Unit)? = null) {
    when (fieldSchema) {
        is FieldSchema.TextField -> {
            val textFieldController = formState.bind<String>(fieldSchema, "")
            var textFieldState by textFieldController.field
            val error by textFieldController.error
            OutlinedTextField(
                value = textFieldState,
                onValueChange = { it ->
                    textFieldState = it
                    textFieldController.validate()
                },
                isError = error != null,
                label = { Text(fieldSchema.label) },
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )
        }

        is FieldSchema.TextAreaField -> {
            val textAreaFieldController = formState.bind<String>(fieldSchema, "")
            var textAreaState by textAreaFieldController.field
            val error by textAreaFieldController.error
            OutlinedTextField(
                value = textAreaState,
                onValueChange = { it ->
                    textAreaState = it
                    textAreaFieldController.validate()
                },
                isError = error != null,
                label = { Text(fieldSchema.label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height((fieldSchema.maxLines * 24).dp),
                maxLines = fieldSchema.maxLines,
                supportingText = {
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )
        }

        is FieldSchema.NumberField -> {
            val numberFieldController = formState.bind<String>(fieldSchema, "")
            var numFieldState by numberFieldController.field
            val error by numberFieldController.error
            OutlinedTextField(
                value = numFieldState,
                onValueChange = {
                    val digits = it.filter { c -> c.isDigit() }
                    numFieldState = digits
                    numberFieldController.validate()
                },
                isError = error != null,
                label = { Text(fieldSchema.label) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )
        }

        is FieldSchema.SelectField -> {
            var expanded by remember { mutableStateOf(false) }
            val selectFieldController = formState.bind<String>(fieldSchema, "")
            var selectedOption by selectFieldController.field
            val error by selectFieldController.error
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        value = selectedOption,
                        onValueChange = {},
                        readOnly = true,
                        isError = error != null,
                        label = { Text(fieldSchema.label) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        supportingText = {
                            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        fieldSchema.options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                    selectFieldController.validate()
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }

        is FieldSchema.CheckboxField -> {
            val checkboxFieldController = formState.bind<Boolean>(fieldSchema, fieldSchema.checked)
            var checkedState by checkboxFieldController.field
            val error by checkboxFieldController.error
            Checkbox(
                checked = checkedState,
                onCheckedChange = {
                    checkedState = it
                    checkboxFieldController.validate()
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = fieldSchema.label)
            if (error != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        }

        is FieldSchema.DatePickerField -> {
            val datePickerController = formState.bind<String>(fieldSchema, "")
            var selectedDate by datePickerController.field
            val error by datePickerController.error

            var showDatePicker by remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState()
            selectedDate = datePickerState.selectedDateMillis?.let {
                convertMillisToDate(it)
            } ?: ""

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = selectedDate,
                    onValueChange = {},
                    label = { Text(fieldSchema.label) },
                    isError = error != null,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = !showDatePicker }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select date"
                            )
                        }
                    },
                    supportingText = {
                        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    }
                )

                if (showDatePicker) {
                    Popup(
                        onDismissRequest = {
                            showDatePicker = false
                            datePickerController.validate()
                        },
                        alignment = Alignment.TopStart
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp)
                        ) {
                            @OptIn(ExperimentalMaterial3Api::class)
                            DatePicker(
                                state = datePickerState,
                                showModeToggle = false
                            )
                        }
                    }
                }
            }
        }

        is FieldSchema.ButtonField -> {
            Button(
                onClick = { onAction?.invoke() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(fieldSchema.label)
            }
        }
    }
}


fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}