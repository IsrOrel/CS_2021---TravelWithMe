package com.example.travelwithme.Data

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AttractionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAttraction(attraction: Attraction_Data)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAttractions(attractions: List<Attraction_Data>)

    @Update
    fun updateAttraction(attraction: Attraction_Data)

    @Update
    fun updateAttractions(attractions: List<Attraction_Data>)

    @Delete
    fun deleteAttraction(attraction: Attraction_Data)

    @Query("DELETE FROM attractions")
    fun clearAllAttractions()

    @Query("SELECT * FROM attractions")
    fun getAllAttractions(): List<Attraction_Data>

    @Query("SELECT * FROM attractions WHERE title = :title")
    suspend fun getAttractionByTitle(title: String): Attraction_Data?

    @Query("SELECT * FROM attractions WHERE city = :city")
    fun getAttractionsForCity(city: String): LiveData<List<Attraction_Data>>

    @Query("SELECT * FROM attractions WHERE city = :destination")
    fun getAttractionsForDestination(destination: String): LiveData<List<Attraction_Data>>

    @Query("SELECT * FROM attractions WHERE city = :city AND category = :category")
    fun getAttractionsForCityAndCategory(
        city: String,
        category: String
    ): LiveData<List<Attraction_Data>>

    @Query("SELECT * FROM attractions WHERE category = :category")
    fun getAttractionsByCategory(category: String): LiveData<List<Attraction_Data>>

    @Query("SELECT * FROM attractions WHERE city = :city AND category IN (:categories)")
    fun getAttractionsForCityAndCategories(city: String, categories: List<String>): LiveData<List<Attraction_Data>>
}


