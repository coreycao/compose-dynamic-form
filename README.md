# compose-dynamic-form

Schema driven dynamic form component for Compose platform.

`compose-dynamic-form` 是一个基于 Jetpack Compose 的动态表单组件库，旨在提供灵活、可扩展的表单构建解决方案。它支持通过 JSON Schema 定义表单结构，并提供多种内置控件和验证规则。

## 特性

- **Schema 驱动**：通过 JSON 定义表单，易于扩展和维护。
- **多种控件**：内置文本、数字、下拉选择、多行文本、复选框和按钮等常用控件。
- **内置校验**：支持必填、最小/最大长度和正则表达式等多种校验规则。
- **状态管理**：内置 `FormState` 和 `SchemaFormViewModel`，轻松管理表单状态和校验逻辑。
- **轻松集成**：仅需一个 Composable 函数即可在您的应用中集成动态表单。

## 使用

在您的项目中，直接使用 `SchemaFormScreen` Composable，并传入相应的 JSON Schema 即可得到一个 Material Design 风格的动态表单。

```kotlin
@Composable
fun MainScreen() {
    val jsonSchema = """...""" // 您的 JSON Schema
    val formSchema = Json.decodeFromString<FormSchema>(jsonSchema)

    SchemaFormScreen(
        formSchema = formSchema,
        onSubmit = { formResult ->
            // 处理表单提交结果
        },
        onAction = { command ->
            // 处理自定义按钮事件
        }
    )
}
```

## JSON Schema

`compose-dynamic-form` 使用自定义的 JSON Schema 来定义表单。以下是一个包含所有支持控件和校验规则的示例：

```json
{
  "type": "form",
  "title": "用户信息登记",
  "fields": [
    {
      "type": "text",
      "label": "姓名",
      "name": "name",
      "validation": {
        "required": true,
        "minLength": 2,
        "maxLength": 10
      }
    },
    {
      "type": "number",
      "label": "年龄",
      "name": "age",
      "validation": {
        "required": true
      }
    },
    {
      "type": "select",
      "label": "地区",
      "name": "region",
      "options": [
        "北京",
        "上海",
        "广州"
      ],
      "validation": {
        "required": true
      }
    },
    {
      "type": "textarea",
      "label": "自我介绍",
      "name": "bio",
      "validation": {
        "required": true,
        "minLength": 10
      },
      "maxLines": 4
    },
    {
      "type": "checkbox",
      "label": "接受条款",
      "name": "terms",
      "validation": {
        "requiredChecked": true
      }
    },
    {
      "type": "button",
      "label": "提交",
      "name": "btn_submit",
      "action": "submit"
    },
    {
      "type": "button",
      "label": "重置",
      "name": "btn_reset",
      "action": "reset"
    }
  ]
}
```

## 截图

![](./screenshot/screenshot_01.png)