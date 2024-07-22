package com.example.travelwithme.Data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.travelwithme.CategoryIcons

@Entity(tableName = "attractions")
data class Attraction_Data(
    @PrimaryKey val title: String,
    val image: Int,
    val description: String,
    val city: String,
    val category: String,
    val address: String,
    val iconResId: Int = CategoryIcons.getIconForCategory(category)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attraction_Data

        if (title != other.title) return false
        if (city != other.city) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + city.hashCode()
        return result
    }
}