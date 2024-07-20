package com.example.travelwithme.Data

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

    // New methods for Hotels conversion
    @TypeConverter
    fun fromHotelsList(value: String): List<Hotels> {
        if (value.isEmpty()) return emptyList()
        return value.split(";").map {
            val (name, address, checkinDate, checkoutDate) = it.split(",")
            Hotels(name, address, Date(checkinDate.toLong()), Date(checkoutDate.toLong()))
        }
    }

    @TypeConverter
    fun toHotelsList(list: List<Hotels>): String {
        return list.joinToString(";") { "${it.name},${it.address},${it.CheckinDate.time},${it.CheckoutDate.time}" }
    }
    @TypeConverter
    fun fromChecklistItemList(value: String): List<ChecklistItem> {
        if (value.isEmpty()) return emptyList()
        return value.split(";").map {
            val (id, text, isChecked) = it.split(",")
            ChecklistItem(id.toLong(), text, isChecked.toBoolean())
        }
    }

    @TypeConverter
    fun toChecklistItemList(list: List<ChecklistItem>): String {
        return list.joinToString(";") { "${it.id},${it.text},${it.isChecked}" }
    }
}