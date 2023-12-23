package ir.ayantech.whygoogle.helper

import android.os.Bundle
import android.os.Parcelable
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class FragmentArgumentDelegate<T : Any?>(
    private val key: String? = null
) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val arguments = (thisRef as? WhyGoogleFragment<*>)?.arguments
        return arguments?.get(key ?: property.name) as? T
            ?: throw IllegalStateException("Property ${property.name} not initialized")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val arguments = (thisRef as? WhyGoogleFragment<*>)?.arguments ?: Bundle()
        val finalKey = this.key ?: property.name

        when (value) {
            is String -> arguments.putString(finalKey, value)
            is Int -> arguments.putInt(finalKey, value)
            is Long -> arguments.putLong(finalKey, value)
            is Double -> arguments.putDouble(finalKey, value)
            is Boolean -> arguments.putBoolean(finalKey, value)
            is Float -> arguments.putFloat(finalKey, value)
            is Char -> arguments.putChar(finalKey, value)
            is Short -> arguments.putShort(finalKey, value)
            is Byte -> arguments.putByte(finalKey, value)
            is Serializable -> arguments.putSerializable(finalKey, value)
            is Parcelable -> arguments.putParcelable(finalKey, value)
        }

        (thisRef as? WhyGoogleFragment<*>)?.arguments = arguments
    }
}

inline fun <reified T : Any?> fragmentArgument(key: String? = null) = FragmentArgumentDelegate<T>(key)