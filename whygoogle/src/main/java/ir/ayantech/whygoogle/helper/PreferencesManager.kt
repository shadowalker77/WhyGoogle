package ir.ayantech.whygoogle.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class PreferencesManager private constructor(context: Context) {

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

    inline fun <reified T> read(fieldName: String, defaultValue: Any = 0): T {
        return when (T::class.java.toString()) {
            "class java.lang.String" -> readStringFromSharedPreferences(fieldName) as T
            "class java.lang.Boolean" -> readBooleanFromSharedPreferences(fieldName) as T
            "class java.lang.Long" -> readLongFromSharedPreferences(fieldName) as T
            "class java.lang.Integer" -> readIntFromSharedPreferences(fieldName) as T
            "class java.lang.Float" -> readFloatFromSharedPreferences(fieldName) as T
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

    fun readStringFromSharedPreferences(field: String): String {
        return sharedPreferences?.getString(field, "") ?: ""
    }

    private fun <T> saveComplexList(field: String, items: List<T>) {
        saveToSharedPreferences(
            field,
            items.foldList()
        )
    }

    fun readBooleanFromSharedPreferences(field: String): Boolean {
        return sharedPreferences?.getBoolean(field, false) ?: false
    }

    fun readLongFromSharedPreferences(field: String): Long {
        return sharedPreferences?.getLong(field, 0L) ?: 0L
    }

    fun readIntFromSharedPreferences(field: String): Int {
        return sharedPreferences?.getInt(field, 0) ?: 0
    }

    fun readFloatFromSharedPreferences(field: String): Float {
        return sharedPreferences?.getFloat(field, 0f) ?: 0f
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