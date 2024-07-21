package com.example.travelwithme.Data

import androidx.lifecycle.LiveData
import androidx.room.*

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
    @Update
    fun updateAttractions(attractions: List<Attraction_Data>)


    @Delete
    fun deleteAttraction(attraction: Attraction_Data)

    @Query("SELECT * FROM attractions WHERE city = :city")
    fun getAttractionsForCity(city: String): LiveData<List<Attraction_Data>>

    @Query("SELECT * FROM attractions WHERE city = :city AND category = :category")
    fun getAttractionsForCityAndCategory(city: String, category: String): LiveData<List<Attraction_Data>>

    @Query("SELECT COUNT(*) FROM attractions WHERE title = :title AND city = :city AND address = :address")
    fun countAttraction(title: String, city: String, address: String): Int

    @Query("SELECT * FROM attractions WHERE title = :title AND city = :city LIMIT 1")
    fun getAttractionByTitleAndCity(title: String, city: String): Attraction_Data?

    @Query("SELECT * FROM attractions WHERE title = :title LIMIT 1")
    fun getAttractionByTitle(title: String): Attraction_Data?
}