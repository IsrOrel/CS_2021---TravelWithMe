package com.example.travelwithme.Data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AttractionViewModel(application: Application) : AndroidViewModel(application) {
    private val database: TravelDatabase by lazy {
        TravelDatabase.getInstance(application.applicationContext)
    }
    private val attractionDao = database.attractionDao()
    private val userDao = database.userDao()

    fun getAttractionsForCity(city: String): LiveData<List<Attraction_Data>> {
        return attractionDao.getAttractionsForCity(city)
    }

    fun getAttractionsForDestination(destination: String): LiveData<List<Attraction_Data>> {
        return attractionDao.getAttractionsForDestination(destination)
    }

    fun getAttractionsForCityAndCategory(city: String, category: String): LiveData<List<Attraction_Data>> {
        return attractionDao.getAttractionsForCityAndCategory(city, category)
    }

    fun insertAttraction(attraction: Attraction_Data) {
        viewModelScope.launch(Dispatchers.IO) {
            attractionDao.insertAttraction(attraction)
        }
    }

    fun insertAttractions(attractions: List<Attraction_Data>) {
        viewModelScope.launch(Dispatchers.IO) {
            // First, get existing attractions from the database
            val existingAttractions = attractionDao.getAllAttractions()
            val uniqueAttractions = attractions.distinctBy { it.title + it.city }

            // Check for duplicates and only insert unique attractions
            val toInsert = uniqueAttractions.filter { newAttraction ->
                existingAttractions.none { it.title == newAttraction.title && it.city == newAttraction.city }
            }

            attractionDao.insertAttractions(toInsert)
        }
    }

    fun updateAttraction(attraction: Attraction_Data) {
        viewModelScope.launch(Dispatchers.IO) {
            attractionDao.updateAttraction(attraction)
        }
    }

    fun updateAttractions(attractions: List<Attraction_Data>) {
        viewModelScope.launch(Dispatchers.IO) {
            attractionDao.updateAttractions(attractions)
        }
    }

    fun deleteAttraction(attraction: Attraction_Data) {
        viewModelScope.launch(Dispatchers.IO) {
            attractionDao.deleteAttraction(attraction)
        }
    }

    fun addAttractionToCalendar(userEmail: String, attraction: SelectedAttraction) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.addAttraction(userEmail, attraction)
        }
    }
    fun getAllAttractionsForCity(destination: String): LiveData<List<Attraction_Data>> {
        return attractionDao.getAttractionsForCity(destination)
    }

    suspend fun getAttractionByTitle(title: String): Attraction_Data? {
        return attractionDao.getAttractionByTitle(title)
    }

    fun removeDuplicateAttractions() {
        viewModelScope.launch(Dispatchers.IO) {
            val allAttractions = attractionDao.getAllAttractions()
            val uniqueAttractions = allAttractions.distinctBy { it.title + it.city }

            // Clear the database and re-insert unique attractions
            attractionDao.clearAllAttractions()
            attractionDao.insertAttractions(uniqueAttractions)
        }
    }
    fun clearAndInsertAttractions(attractions: List<Attraction_Data>) {
        viewModelScope.launch(Dispatchers.IO) {
            // Clear existing data
            attractionDao.clearAllAttractions()
            // Insert deduplicated data
            val uniqueAttractions = attractions.distinctBy { it.title + it.city }
            attractionDao.insertAttractions(uniqueAttractions)
        }
    }
}
