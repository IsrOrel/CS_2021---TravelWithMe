package com.example.travelwithme.Data// Converters.kt
import androidx.room.TypeConverter
import java.util.Date
class User_Data_Convertors {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromSelectedAttractionList(value: String): List<SelectedAttraction> {
        if (value.isEmpty()) return emptyList()
        return value.split(";").map {
            val (title, plannedDate, plannedTime) = it.split(",")
            SelectedAttraction(title, Date(plannedDate.toLong()), plannedTime)
        }
    }

    @TypeConverter
    fun toSelectedAttractionList(list: List<SelectedAttraction>): String {
        return list.joinToString(";") { "${it.title},${it.plannedDate.time},${it.plannedTime}" }
    }
}