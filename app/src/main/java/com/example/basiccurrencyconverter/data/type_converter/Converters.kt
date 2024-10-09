package com.example.basiccurrencyconverter.data.type_converter

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun convertToString(map: Map<String, Double>?): String {
        if (map.isNullOrEmpty()) {
            return "" // Return an empty string for null or empty maps
        }

        val stringBuilder = StringBuilder()
        for ((key, value) in map) {
            stringBuilder.append("$key to $value, ")
        }
        return stringBuilder.removeSuffix(", ").toString()
    }

    @TypeConverter
    fun convertToMap(string: String?): Map<String, Double> {
        if (string.isNullOrEmpty()) {
            return emptyMap() // Return an empty map for null or empty strings
        }

        val newMap = mutableMapOf<String, Double>()
        val splitString = string.split(", ")

        for (pair in splitString) {
            try {
                val (key, value) = pair.split(" to ").map { it.trim() }
                if (key.isNotBlank() && value.isNotBlank()) {
                    newMap[key] = value.toDouble()
                }
            } catch (e: Exception) {
                println("Error parsing pair '$pair': ${e.message}")
                throw IllegalArgumentException(e)
            }
        }
        return newMap
    }
}
