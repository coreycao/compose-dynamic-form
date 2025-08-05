package com.corey.dynamicform

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.corey.form.FormSchema
import com.corey.form.SchemaFormScreen
import kotlinx.serialization.json.Json

/**
 * @author Yeung
 * @date 2025/8/5
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val formSchema by remember {
        mutableStateOf(loadSampleFormSchema())
    }
    SchemaFormScreen(modifier = modifier, formSchema = formSchema, onSubmit = {
        Log.d("MainScreen", "Form submitted with values: $it")
    })
}

val json = Json { ignoreUnknownKeys = true }

fun loadSampleFormSchema(): FormSchema {
    val formSchema = """
        {
          "type": "form",
          "title": "用户信息登记",
          "fields": [
            { "type": "text", "label": "姓名", "name": "name", "validation": { "required": false } },
            { "type": "number", "label": "年龄", "name": "age", "validation": { "required": false } },
            { "type": "select", "label": "地区", "name": "region", "options": ["北京", "上海", "广州"], "validation": { "required": true } },
            { "type": "textarea", "label": "自我介绍", "name": "bio", "validation": { "required": true, "minLength": 10 }, "maxLines": 4 },
            { "type": "checkbox", "label": "接受条款", "name": "terms", "validation": { "requiredChecked": true } },
            { "type": "button", "label": "提交", "name": "btn_submit", "action": "submit" },
            { "type": "button", "label": "重置", "name": "btn_reset", "action": "reset" }
          ]
        }
    """.trimIndent()

    return json.decodeFromString(formSchema)
}