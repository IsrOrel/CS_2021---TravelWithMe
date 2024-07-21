package com.example.travelwithme.Data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AttractionDao {

    @Query("SELECT * FROM attractions")
    fun getAllAttractions(): LiveData<List<Attraction_Data>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttraction(attraction: Attraction_Data)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttractions(attractions: List<Attraction_Data>)

    @Update
    suspend fun updateAttraction(attraction: Attraction_Data)

    @Delete
    suspend fun deleteAttraction(attraction: Attraction_Data)

    @Query("SELECT * FROM attractions WHERE city = :city")
    fun getAttractionsForCity(city: String): LiveData<List<Attraction_Data>>

    @Query("SELECT * FROM attractions WHERE city = :city AND category = :category")
    fun getAttractionsForCityAndCategory(city: String, category: String): LiveData<List<Attraction_Data>>

    @Query("SELECT COUNT(*) FROM attractions WHERE title = :title AND city = :city AND address = :address")
    suspend fun countAttraction(title: String, city: String, address: String): Int
}
