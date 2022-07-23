package ir.ayantech.whygoogle.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

internal class PreferencesManager private constructor(context: Context) {

    private var sharedPreferences: SharedPreferences? = null

    init {
        sharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    companion object {
        private var preferencesManager: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager =
            preferencesManager ?: PreferencesManager(context).also { preferencesManager = it }
    }

    fun save(fieldName: String, value: Any) {
        when (value) {
            is String -> saveToSharedPreferences(fieldName, value)
            is Boolean -> saveToSharedPreferences(fieldName, value)
            is Long -> saveToSharedPreferences(fieldName, value)
            is Int -> saveToSharedPreferences(fieldName, value)
            is Float -> saveToSharedPreferences(fieldName, value)
            is List<*> -> saveComplexList(fieldName, value)
            else -> throw Exception("cannot save")
        }
    }

    inline fun <reified T> read(fieldName: String, defaultValue: T? = null): T {
        return when (T::class.java.toString()) {
            "class java.lang.String" -> readStringFromSharedPreferences(
                fieldName,
                (defaultValue ?: "") as String
            ) as T
            "class java.lang.Boolean" -> readBooleanFromSharedPreferences(
                fieldName,
                (defaultValue ?: false) as Boolean
            ) as T
            "class java.lang.Long" -> readLongFromSharedPreferences(
                fieldName,
                (defaultValue ?: 0L) as Long
            ) as T
            "class java.lang.Integer" -> readIntFromSharedPreferences(
                fieldName,
                (defaultValue ?: 0) as Int
            ) as T
            "class java.lang.Float" -> readFloatFromSharedPreferences(
                fieldName,
                (defaultValue ?: 0f) as Float
            ) as T
            else -> try {
                defaultValue as T
            } catch (e: java.lang.Exception) {
                throw Exception("cannot read")
            }
        }
    }

    private fun saveToSharedPreferences(fieldName: String, value: String) {
        sharedPreferences?.edit()?.putString(fieldName, value)?.apply()
    }

    private fun saveToSharedPreferences(fieldName: String, value: Boolean) {
        sharedPreferences?.edit()?.putBoolean(fieldName, value)?.apply()
    }

    private fun saveToSharedPreferences(fieldName: String, value: Long?) {
        sharedPreferences?.edit()?.putLong(fieldName, value!!)?.apply()
    }

    private fun saveToSharedPreferences(fieldName: String, value: Int?) {
        sharedPreferences?.edit()?.putInt(fieldName, value!!)?.apply()
    }

    private fun saveToSharedPreferences(fieldName: String, value: Float?) {
        sharedPreferences?.edit()?.putFloat(fieldName, value!!)?.apply()
    }

    private fun readStringFromSharedPreferences(field: String, defaultValue: String = ""): String {
        return sharedPreferences?.getString(field, defaultValue) ?: ""
    }

    private fun readBooleanFromSharedPreferences(
        field: String,
        defaultValue: Boolean = false
    ): Boolean {
        return sharedPreferences?.getBoolean(field, defaultValue) ?: false
    }

    private fun readLongFromSharedPreferences(field: String, defaultValue: Long = 0L): Long {
        return sharedPreferences?.getLong(field, defaultValue) ?: 0L
    }

    private fun readIntFromSharedPreferences(field: String, defaultValue: Int = 0): Int {
        return sharedPreferences?.getInt(field, defaultValue) ?: 0
    }

    private fun readFloatFromSharedPreferences(field: String, defaultValue: Float = 0f): Float {
        return sharedPreferences?.getFloat(field, defaultValue) ?: 0f
    }

    fun <T> saveComplexList(field: String, items: List<T>) {
        saveToSharedPreferences(
            field,
            items.foldList()
        )
    }

    inline fun <reified T> getComplexList(
        field: String,
        defaultValue: List<T> = arrayListOf()
    ): ArrayList<T> =
        ArrayList(readStringFromSharedPreferences(field).let {
            if (it.isNotEmpty())
                it.split("#^#")
                    .toList().map { Gson().fromJson(it, T::class.java) }
            else
                defaultValue
        })

    private fun <T> List<T>.foldList(): String = this.map { Gson().toJson(it) }.let {
        if (it.isEmpty()) ""
        else it.reduce { acc, s -> "$acc#^#$s" }
    }
}