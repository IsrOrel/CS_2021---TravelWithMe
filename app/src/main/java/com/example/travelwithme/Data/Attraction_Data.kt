package com.example.travelwithme.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attractions")
data class Attraction_Data(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val image: Int,
    val title: String,
    val description: String,
    val place: String
)
