// AttractionDao.kt
package com.example.travelwithme

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AttractionDao {
    @Query("SELECT * FROM attractions")
    fun getAllAttractions(): LiveData<List<Attraction_Data>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttraction(attraction: Attraction_Data)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttractions(attractions: List<Attraction_Data>)

    @Update
    fun updateAttraction(attraction: Attraction_Data)

    @Query("UPDATE attractions SET image = :image, title = :title, description = :description, place = :place WHERE id = :id")
    fun updateAttractionById(id: Int, image: Int, title: String, description: String, place: String)

    @Delete
    fun deleteAttraction(attraction: Attraction_Data)

}
