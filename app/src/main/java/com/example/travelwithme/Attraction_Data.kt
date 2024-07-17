package com.example.travelwithme

import androidx.room.Entity

@Entity(tableName = "attractions")
data class Attraction_Data(
    val image: Int,
    val title: String,
    val description: String,
    val place: String
)