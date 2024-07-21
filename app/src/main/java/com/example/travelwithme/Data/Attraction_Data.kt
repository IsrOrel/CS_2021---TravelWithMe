package com.example.travelwithme.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attractions")
data class Attraction_Data(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val image: Int,
    val title: String,
    val description: String,
    val city: String,
    val category: String,
    val address: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attraction_Data

        if (id != other.id) return false
        if (title != other.title) return false
        if (city != other.city) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + city.hashCode()
        return result
    }
}