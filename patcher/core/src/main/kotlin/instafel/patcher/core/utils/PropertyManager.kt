package instafel.patcher.core.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Properties
import kotlin.system.exitProcess

class PropertyManager(val propertyFile: File) {
    private lateinit var properties: Properties

    init {
        parseProperty()
    }

    @Throws(IOException::class)
    private fun parseProperty() {
        properties = Properties()
        FileInputStream(propertyFile).use { inputStream ->
            properties.load(inputStream)
        }
    }

    fun getProperties(): Properties = properties
    fun getString(key: String, defaultValue: String): String =
        properties.getProperty(key, defaultValue)
    fun getInteger(key: String, defaultValue: Int): Int {
        return try {
            properties.getProperty(key, defaultValue.toString())?.toInt() ?: defaultValue
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    fun add(key: String, value: Any, comment: String? = null) {
        properties[key] = value
    }
    fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        properties.getProperty(key, defaultValue.toString())?.toBoolean() ?: defaultValue
    fun addString(key: String, value: String) = properties.setProperty(key, value)
    fun addInteger(key: String, value: Int) = properties.setProperty(key, value.toString())
    fun addBoolean(key: String, value: Boolean) = properties.setProperty(key, value.toString())

    fun save(comment: String? = null) {
        try {
            FileOutputStream(propertyFile).use { output ->
                properties.store(output, comment)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.severe("Error while saving property file $propertyFile")
            exitProcess(-1)
        }
    }
}