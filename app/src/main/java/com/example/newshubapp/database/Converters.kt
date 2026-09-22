package com.example.newshubapp.data.database

import androidx.room.TypeConverter
import com.example.newshubapp.data.models.Source
import com.google.gson.Gson

/**
 * Type Converters for Room Database
 * Converts complex types to simple types for storage
 * Reference: https://developer.android.com/training/data-storage/room/referencing-data
 */
class Converters {

    // ========== SOURCE CONVERTER ==========
    // Room can't store the Source object directly, so we convert it to/from JSON

    private val gson = Gson()

    @TypeConverter
    fun fromSource(source: Source?): String? {
        return source?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toSource(sourceString: String?): Source? {
        return sourceString?.let { gson.fromJson(it, Source::class.java) }
    }

    // ========== LIST<STRING> CONVERTER ==========
    // Used if any entity has a List<String> property

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return if (value.isNullOrEmpty()) emptyList() else value.split(",")
    }
}