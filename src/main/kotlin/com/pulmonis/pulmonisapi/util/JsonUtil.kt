package com.pulmonis.pulmonisapi.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.pulmonis.pulmonisapi.exception.JsonUtilException
import java.io.IOException
import java.text.SimpleDateFormat

object JsonUtil {
    @JvmStatic
    fun toPrettyJson(obj: Any): String? {
        return toJson(obj, usePrettyWritter = true, formatDates = true)
    }

    /**
     *  Writes the input obj into JSON string
     */
    @JvmStatic
    @JvmOverloads
    @Throws(JsonUtilException::class)
    fun toJson(obj: Any, usePrettyWritter: Boolean = false, formatDates: Boolean = false): String? {
        try {
            val mapper = ObjectMapper().also {
                it.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            }
            if (formatDates) {
                mapper.dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm a z")
            }

            var writer = mapper.writer()

            if (usePrettyWritter) {
                writer = writer.withDefaultPrettyPrinter()
            }
            return writer.writeValueAsString(obj)
        } catch (e: JsonProcessingException) {
            throw JsonUtilException("Failed to parse $obj to JSON: ${e.message}", e)
        }
    }

    /**
     *  Reads JSON string and deserializes into given clazz
     */
    @JvmStatic
    @Throws(JsonUtilException::class)
    fun <T> fromJson(json: String?, clazz: TypeReference<T>): T? {
        var data: T? = null
        if (json.isNullOrEmpty()) {
            return data
        }
        try {
            val mapper = ObjectMapper()
            data = mapper.readValue<T>(json, clazz)
        } catch (e: IOException) {
            throw JsonUtilException("Failed to parse from JSON $json : ${e.message}", e)
        }
        return data
    }

    /**
     * Checks if json is empty
     */
    @JvmStatic
    fun isJsonEmpty(json: String?): Boolean {
        if (json.isNullOrEmpty()) {
            return true
        }
        return json == "{}" || json == "[]" || json == ""
    }
}
