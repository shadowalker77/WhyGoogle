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

    fun oldSave(fieldName: String, value: Any) {
        when (value) {
            is String -> noEncSaveToSharedPreferences(fieldName, value)
            is Boolean -> noEncSaveToSharedPreferences(fieldName, value)
            is Long -> noEncSaveToSharedPreferences(fieldName, value)
            is Int -> noEncSaveToSharedPreferences(fieldName, value)
            is Float -> noEncSaveToSharedPreferences(fieldName, value)
            is List<*> -> saveComplexList(fieldName, value)
            else -> throw Exception("cannot save")
        }
    }

    fun save(fieldName: String, value: Any) {
        when (value) {
            is String -> saveToSharedPreferences("enc_$fieldName", value)
            is Boolean -> saveToSharedPreferences("enc_$fieldName", value)
            is Long -> saveToSharedPreferences("enc_$fieldName", value)
            is Int -> saveToSharedPreferences("enc_$fieldName", value)
            is Float -> saveToSharedPreferences("enc_$fieldName", value)
            is List<*> -> saveComplexList("enc_$fieldName", value)
            else -> throw Exception("cannot save")
        }
    }

    inline fun <reified T> oldRead(fieldName: String, defaultValue: T? = null): T {

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

    inline fun <reified T> read(fieldName: String, defaultValue: T? = null): T {
        return when (T::class.java.toString()) {
            "class java.lang.String" -> readStringFromSharedPreferences(
                "enc_$fieldName",
                (defaultValue ?: "") as String
            ) as T
            "class java.lang.Boolean" -> readBooleanFromSharedPreferences(
                "enc_$fieldName",
                (defaultValue ?: false) as Boolean
            ) as T
            "class java.lang.Long" -> readLongFromSharedPreferences(
                "enc_$fieldName",
                (defaultValue ?: 0L) as Long
            ) as T
            "class java.lang.Integer" -> readIntFromSharedPreferences(
                "enc_$fieldName",
                (defaultValue ?: 0) as Int
            ) as T
            "class java.lang.Float" -> readFloatFromSharedPreferences(
                "enc_$fieldName",
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
        sharedPreferences?.edit()?.putString(fieldName, value.encryptAES(fieldName))?.apply()
    }

    private fun saveToSharedPreferences(fieldName: String, value: Boolean) {
        sharedPreferences?.edit()
            ?.putString(fieldName, value.toString().encryptAES(fieldName))?.apply()
    }

    private fun saveToSharedPreferences(fieldName: String, value: Long) {
        sharedPreferences?.edit()
            ?.putString(fieldName, value.toString().encryptAES(fieldName))?.apply()
    }

    private fun saveToSharedPreferences(fieldName: String, value: Int?) {
        sharedPreferences?.edit()
            ?.putString(fieldName, value.toString().encryptAES(fieldName))?.apply()
    }

    private fun saveToSharedPreferences(fieldName: String, value: Float?) {
        sharedPreferences?.edit()
            ?.putString(fieldName, value.toString().encryptAES(fieldName))?.apply()
    }

    private fun noEncSaveToSharedPreferences(fieldName: String, value: String) {
        sharedPreferences?.edit()?.putString(fieldName, value)?.apply()
    }

    private fun noEncSaveToSharedPreferences(fieldName: String, value: Boolean) {
        sharedPreferences?.edit()?.putBoolean(fieldName, value)?.apply()
    }

    private fun noEncSaveToSharedPreferences(fieldName: String, value: Long) {
        sharedPreferences?.edit()?.putLong(fieldName, value)?.apply()
    }

    private fun noEncSaveToSharedPreferences(fieldName: String, value: Int) {
        sharedPreferences?.edit()?.putInt(fieldName, value)?.apply()
    }

    private fun noEncSaveToSharedPreferences(fieldName: String, value: Float) {
        sharedPreferences?.edit()?.putFloat(fieldName, value)?.apply()
    }

    private inline fun <reified T : Any> convertOldValues(field: String) {
        if (field.startsWith("enc")) {
            val oldField = field.replace("enc_", "")
            if (sharedPreferences?.contains(oldField) == true) {
                val oldValue = oldRead<T>(oldField)
                save(oldField, oldValue)
                sharedPreferences?.edit()?.remove(oldField)?.apply()
            }
        }
    }

    fun readStringFromSharedPreferences(field: String, defaultValue: String = ""): String {
        convertOldValues<String>(field)
        return if (field.startsWith("enc")) sharedPreferences?.getString(
            field,
            defaultValue
        )?.decryptAES(field) ?: ""
        else sharedPreferences?.getString(field, defaultValue) ?: ""
    }

    fun readBooleanFromSharedPreferences(
        field: String,
        defaultValue: Boolean = false
    ): Boolean {
        convertOldValues<Boolean>(field)
        return if (field.startsWith("enc")) sharedPreferences?.getString(
            field,
            defaultValue.toString()
        )?.decryptAES(field)
            .toBoolean()
        else
            sharedPreferences?.getBoolean(field, defaultValue) ?: false
    }

    fun readLongFromSharedPreferences(field: String, defaultValue: Long = 0L): Long {
        convertOldValues<Long>(field)
        return if (field.startsWith("enc")) sharedPreferences?.getString(
            field,
            defaultValue.toString()
        )?.decryptAES(field)
            ?.toLong() ?: 0L
        else
            sharedPreferences?.getLong(field, defaultValue) ?: 0L
    }

    fun readIntFromSharedPreferences(field: String, defaultValue: Int = 0): Int {
        convertOldValues<Int>(field)
        return if (field.startsWith("enc")) sharedPreferences?.getString(
            field,
            defaultValue.toString()
        )?.decryptAES(field)
            ?.toInt() ?: 0
        else sharedPreferences?.getInt(field, defaultValue) ?: 0
    }

    fun readFloatFromSharedPreferences(field: String, defaultValue: Float = 0f): Float {
        convertOldValues<Float>(field)
        return if (field.startsWith("enc")) sharedPreferences?.getString(
            field,
            defaultValue.toString()
        )?.decryptAES(field)
            ?.toFloat() ?: 0f
        else sharedPreferences?.getFloat(field, defaultValue) ?: 0f
    }

    fun <T> saveComplexList(field: String, items: List<T>) {
        saveToSharedPreferences(field, items.foldList())
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