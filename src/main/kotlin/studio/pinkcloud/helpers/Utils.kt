package studio.pinkcloud.helpers

import kotlinx.serialization.json.JsonPrimitive

fun JsonPrimitive?.isValid() = this?.isString == true && this.content.isNotEmpty()
