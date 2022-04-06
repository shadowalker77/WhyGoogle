package ir.ayantech.whygoogle.helper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun Any?.isNull() = this == null

fun Any?.isNotNull() = this != null

fun Any.toJsonString(): String {
    return Gson().toJson(this)
}

inline fun <reified T> String.fromJsonToObject(): T {
    val klass = T::class.java
    return if (klass.isAssignableFrom(List::class.java))
        Gson().fromJson(
            this,
            object : TypeToken<T>() {
            }.type
        )
    else
        Gson().fromJson(this, T::class.java)
}