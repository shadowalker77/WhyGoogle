package ir.ayantech.whygoogle.helper

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import java.io.Serializable

class FragmentArgumentDelegate<T : Any>(
    private val defaultValue: T? = null,
    private val key: String? = null
) : ReadWriteProperty<Fragment, T> {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val finalKey = this.key ?: property.name
        return thisRef.arguments?.get(finalKey) as? T
            ?: defaultValue
            ?: throw IllegalStateException("Property ${property.name} not initialized")
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        val finalKey = this.key ?: property.name
        val arguments = thisRef.arguments ?: Bundle()
        arguments.putArgument(finalKey, value)
        thisRef.arguments = arguments
    }
}

class FragmentNullableArgumentDelegate<T : Any?>(
    private val defaultValue: T?,
    private val key: String? = null
) : ReadWriteProperty<Fragment, T> {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val finalKey = this.key ?: property.name
        return thisRef.arguments?.get(finalKey) as? T ?: defaultValue ?: getDefault(property)
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        val finalKey = this.key ?: property.name
        val arguments = thisRef.arguments ?: Bundle()
        arguments.putArgument(finalKey, value)
        thisRef.arguments = arguments
    }

    private fun getDefault(property: KProperty<*>): T {
        if (property.returnType.isMarkedNullable) {
            @Suppress("UNCHECKED_CAST")
            return null as T
        } else {
            throw IllegalStateException("Property ${property.name} not initialized")
        }
    }
}

inline fun <reified T : Any> fragmentArgument(defaultValue: T? = null, key: String? = null) =
    FragmentArgumentDelegate(defaultValue, key)

inline fun <reified T : Any?> nullableFragmentArgument(
    defaultValue: T? = null,
    key: String? = null
) = FragmentNullableArgumentDelegate(defaultValue, key)

fun Bundle.putArgument(key: String?, value: Any?) {
    when (value) {
        null -> putSerializable(key, null)
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Long -> putLong(key, value)
        is Double -> putDouble(key, value)
        is Boolean -> putBoolean(key, value)
        is Float -> putFloat(key, value)
        is Char -> putChar(key, value)
        is Short -> putShort(key, value)
        is Byte -> putByte(key, value)
        is Serializable -> putSerializable(key, value)
        is Parcelable -> putParcelable(key, value)
        else -> throw IllegalArgumentException("Unsupported argument type: ${value::class.java.simpleName}")
    }
}